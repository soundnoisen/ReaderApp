package com.example.feature_auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.auth.AuthError
import com.example.core.domain.model.auth.AuthResult
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.feature_auth.domain.GetCurrentUserUseCase
import com.example.feature_auth.domain.SignInUseCase
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
class LoginViewModel @Inject constructor(
    private val signIn: SignInUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val validateCredentials: ValidateCredentialsUseCase,
    private val networkChecker: NetworkCheckerRepository
): ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>(replay = 1)
    val effect: SharedFlow<LoginEffect> = _effect.asSharedFlow()

    init {
        checkAutoLogin()
    }

    fun handleIntent(intent: LoginIntent) {
        when(intent) {
            is LoginIntent.EmailChanged -> onEmailChange(intent.email)
            is LoginIntent.PasswordChanged -> onPasswordChange(intent.password)
            is LoginIntent.Login -> login()
        }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email)
        }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password)}
    }


    private fun login() {
        val (email, password) = _state.value

        val error = validateCredentials(email, password)

        if (error != null) {
            emitError(error)
            return
        }

        if (!networkChecker.isNetworkAvailable()) {
            emitError(AuthError.Network, canRetry = true)
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when(val result = signIn(email, password)) {
                    is AuthResult.Success -> _effect.emit(LoginEffect.NavigateToMain)
                    is AuthResult.Error -> emitError(result.error)
                }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun emitError(error: AuthError, canRetry: Boolean = false) {
        viewModelScope.launch {
            _effect.emit(LoginEffect.ShowError(error, canRetry))
        }
    }

    private fun checkAutoLogin() {
        if (getCurrentUser() != null) {
            viewModelScope.launch {
                _effect.emit(LoginEffect.NavigateToMain)
            }
        }
    }
}
