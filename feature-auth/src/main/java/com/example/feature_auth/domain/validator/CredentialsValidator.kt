package com.example.feature_auth.domain.validator

import com.example.core.domain.model.auth.AuthError
import jakarta.inject.Inject

class CredentialsValidator @Inject constructor() {

    fun email(email: String): AuthError? {
            return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isBlank())
            AuthError.NotValidEmail
        else null
    }

    fun password(password: String): AuthError? {
        return if (password.length < 6 || password.isBlank())
            AuthError.NotValidPassword
        else null
    }
}