package com.example.feature_auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.auth.AuthError
import com.example.core.domain.model.auth.AuthResult
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.feature_auth.domain.repository.RegisterUseCase
import com.example.feature_auth.domain.validator.CredentialsValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val register: RegisterUseCase,
    private val validator: CredentialsValidator,
    private val networkChecker: NetworkCheckerRepository
): ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterEffect>()
    val effect: SharedFlow<RegisterEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: RegisterIntent) {
        when(intent) {
            is RegisterIntent.EmailChanged -> onEmailChange(intent.email)
            is RegisterIntent.PasswordChanged -> onPasswordChange(intent.password)
            is RegisterIntent.PasswordConfirmChanged -> onPasswordConfirmChange(intent.passwordRepeat)
            is RegisterIntent.Registration -> register()
            is RegisterIntent.LoginClicked -> loginClicked()
        }
    }

    private fun loginClicked() {
        sendEffect(RegisterEffect.NavigateToLogin)
    }

    private fun register() {
        validateInputs()?.let { return emitError(it) }

        val state = _state.value

        if (!networkChecker.isNetworkAvailable())
            return emitError(AuthError.Network, canRetry = true)

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = register(state.email, state.password)) {
                    is AuthResult.Success -> sendEffect(RegisterEffect.RegisterSuccess)
                    is AuthResult.Error -> emitError(result.error)
                }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun validateInputs(): AuthError? {
        val state = _state.value
        return validator.email(state.email)
            ?: validator.password(state.password)
            ?: validator.password(state.passwordConfirm)
            ?: if (state.password != state.passwordConfirm) AuthError.DifferentPasswords else null
    }

    private fun onEmailChange(email: String) {
        val error = if (email.isNotBlank()) validator.email(email) else null
        _state.update { it.copy(email = email, emailError = error) }
    }

    private fun onPasswordChange(password: String) {
        updatePasswordField(password, isConfirm = false)
    }

    private fun onPasswordConfirmChange(passwordConfirm: String) {
        updatePasswordField(passwordConfirm, isConfirm = true)
    }

    private fun updatePasswordField(
        password: String,
        isConfirm: Boolean = false
    ) {
        val state = _state.value
        var error: AuthError? = if (password.isNotBlank()) validator.password(password) else null

        val newState = if (isConfirm) {
            if (error == null && password != state.password) error = AuthError.DifferentPasswords
            state.copy(passwordConfirm = password, passwordConfirmError = error)
        } else {
            val confirmError = if (error == null && password == state.passwordConfirm) null else state.passwordConfirmError
            state.copy(password = password, passwordError = error, passwordConfirmError = confirmError)
        }

        _state.update { newState }
    }

    private fun emitError(error: AuthError, canRetry: Boolean = false) {
        sendEffect(RegisterEffect.ShowError(error, canRetry))
    }

    private fun sendEffect(effect: RegisterEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}