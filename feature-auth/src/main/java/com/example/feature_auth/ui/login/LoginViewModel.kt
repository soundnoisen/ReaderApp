package com.example.feature_auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.auth.AuthError
import com.example.core.domain.model.auth.AuthResult
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.feature_auth.domain.repository.GetCurrentUserUseCase
import com.example.feature_auth.domain.repository.SignInUseCase
import com.example.feature_auth.domain.repository.SignInWithGoogleUseCase
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
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val networkChecker: NetworkCheckerRepository
): ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect: SharedFlow<LoginEffect> = _effect.asSharedFlow()

    init {
        checkAutoLogin()
    }

    fun handleIntent(intent: LoginIntent) {
        when(intent) {
            is LoginIntent.EmailChanged -> onEmailChange(intent.email)
            is LoginIntent.PasswordChanged -> onPasswordChange(intent.password)
            is LoginIntent.Login -> login()
            is LoginIntent.GoogleLogin -> loginWithGoogle()
            is LoginIntent.RegisterClicked -> registerClicked()
        }
    }

    private fun registerClicked() {
        sendEffect(LoginEffect.NavigateToRegister)
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password)}
    }

    private fun login() {
        validateInputs()?.let { return emitError(it) }

        val (email, password) = state.value

        performLogin(
            setLoading = { loading -> _state.update { it.copy(isLoading = loading) } },
            setLoginType = { signIn(email, password) }
        )
    }

    fun loginWithGoogle() {
        performLogin(
            setLoading = { loading -> _state.update { it.copy(isGoogleLoading = loading) } },
            setLoginType = { signInWithGoogle() }
        )
    }

    private fun performLogin(
        setLoading: (Boolean) -> Unit,
        setLoginType: suspend () -> AuthResult
    ) {
        if (!networkChecker.isNetworkAvailable()) return emitError(AuthError.Network, true)

        viewModelScope.launch {
            setLoading(true)
            try {
                when(val result = setLoginType()) {
                    is AuthResult.Success -> _state.update { it.copy(isLoggedIn = true) }
                    is AuthResult.Error -> emitError(result.error)
                }
            } finally {
                setLoading(false)
            }
        }
    }

    private fun validateInputs(): AuthError? {
        val state = _state.value
        if (state.email.isBlank() || state.password.isBlank()) {
            return AuthError.FieldsIsBlank
        }
        return null
    }

    private fun emitError(error: AuthError, canRetry: Boolean = false) {
        sendEffect(LoginEffect.ShowError(error, canRetry))
    }

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            if (getCurrentUser() != null) {
                _state.update { it.copy(isLoggedIn = true) }
            }
        }
    }
}
