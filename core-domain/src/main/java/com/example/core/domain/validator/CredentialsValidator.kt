package com.example.core.domain.validator

import com.example.core.domain.model.auth.AuthError
import jakarta.inject.Inject

class CredentialsValidator @Inject constructor() {

    fun validate(email: String, password: String): AuthError? {
        if (!email.isValidEmail()) return AuthError.NotValidEmail
        if (!password.isValidPassword()) return AuthError.NotValidPassword
        return null
    }

    private fun String.isValidEmail(): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun String.isValidPassword(): Boolean = this.length >= 6
}
