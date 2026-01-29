package com.example.feature_auth.ui.register

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val passwordRepeat: String = "",
    val isLoading: Boolean = false
)