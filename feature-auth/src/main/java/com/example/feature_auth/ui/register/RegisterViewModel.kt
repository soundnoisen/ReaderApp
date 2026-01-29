package com.example.feature_auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.auth.AuthError
import com.example.core.domain.model.auth.AuthResult
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.feature_auth.domain.RegisterUseCase
import com.example.feature_auth.domain.ValidateCredentialsUseCase
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
    private val validateCredentials: ValidateCredentialsUseCase,
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
            is RegisterIntent.PasswordRepeatChanged -> onPasswordRepeatChange(intent.passwordRepeat)
            is RegisterIntent.Registration -> register()
        }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onPasswordRepeatChange(passwordRepeat: String) {
        _state.update { it.copy(passwordRepeat = passwordRepeat) }
    }

    fun register() {
        val (email, password, passwordRepeat) = _state.value

        val error = validateCredentials(email, password)

        if (error != null) {
            emitError(error)
            return
        }

        if (!networkChecker.isNetworkAvailable()) {
            emitError(AuthError.Network, canRetry = true)
            return
        }

        if(password != passwordRepeat) {
            emitError(AuthError.DifferentPasswords)
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when(val result = register(email, password)) {
                    is AuthResult.Success -> _effect.emit(RegisterEffect.NavigateToLogin)
                    is AuthResult.Error -> emitError(result.error)
                }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun emitError(error: AuthError, canRetry: Boolean = false) {
        viewModelScope.launch {
            _effect.emit(RegisterEffect.ShowError(error, canRetry))
        }
    }
}