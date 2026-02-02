package com.example.feature_auth.ui.register

sealed class RegisterIntent {
    data class EmailChanged(val email: String): RegisterIntent()
    data class PasswordChanged(val password: String): RegisterIntent()
    data class PasswordConfirmChanged(val passwordRepeat: String): RegisterIntent()
    object Registration: RegisterIntent()
    object LoginClicked: RegisterIntent()
}