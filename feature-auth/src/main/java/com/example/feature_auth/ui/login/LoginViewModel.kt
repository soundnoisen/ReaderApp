package com.example.feature_auth.ui.login

import android.content.ContentValues.TAG
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.auth.AuthError
import com.example.core.domain.model.auth.AuthResult
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.feature_auth.R
import com.example.feature_auth.domain.GetCurrentUserUseCase
import com.example.feature_auth.domain.SignInUseCase
import com.example.feature_auth.domain.SignInWithGoogleUseCase
import com.example.feature_auth.domain.ValidateCredentialsUseCase
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
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
            is LoginIntent.GoogleLogin -> loginWithGoogle()
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

    fun loginWithGoogle() {
        if (!networkChecker.isNetworkAvailable()) {
            emitError(AuthError.Network)
            return
        }
        viewModelScope.launch {
            val result = signInWithGoogle()
            when (result) {
                is AuthResult.Success -> _effect.emit(LoginEffect.NavigateToMain)
                is AuthResult.Error -> _effect.emit(LoginEffect.ShowError(result.error))
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
