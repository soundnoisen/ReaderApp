package com.example.feature_auth.ui.register

import com.example.core.domain.model.auth.AuthError

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isLoading: Boolean = false,
    val emailError: AuthError? = null,
    val passwordError: AuthError? = null,
    val passwordConfirmError: AuthError? = null
)