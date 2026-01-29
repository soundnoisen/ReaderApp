package com.example.feature_auth.ui.register

import com.example.core.domain.model.auth.AuthError

sealed class RegisterEffect {
    object NavigateToLogin: RegisterEffect()
    data class ShowError(val error: AuthError, val canRetry: Boolean = false): RegisterEffect()
}