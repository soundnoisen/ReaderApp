package com.example.feature_auth.ui.login

import com.example.core.domain.model.auth.AuthError

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val emailError: AuthError? = null,
    val passwordError: AuthError? = null
)
