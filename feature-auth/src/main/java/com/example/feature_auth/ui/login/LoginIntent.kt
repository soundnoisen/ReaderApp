package com.example.feature_auth.ui.login

sealed class LoginIntent() {
    data class EmailChanged(val email: String): LoginIntent()
    data class PasswordChanged(val password: String): LoginIntent()
    object Login: LoginIntent()
}