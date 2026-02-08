package com.example.feature_auth.ui.login


data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)
