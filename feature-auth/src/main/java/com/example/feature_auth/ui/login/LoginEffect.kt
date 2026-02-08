package com.example.feature_auth.ui.login

import com.example.core.domain.model.auth.AuthError


sealed class LoginEffect {
    object NavigateToRegister: LoginEffect()
    data class ShowError(val error: AuthError, val canRetry: Boolean = false): LoginEffect()
}
