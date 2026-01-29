package com.example.feature_auth.domain

import com.example.core.domain.model.auth.AuthError
import com.example.core.domain.validator.CredentialsValidator
import jakarta.inject.Inject

class ValidateCredentialsUseCase @Inject constructor(
    private val validator: CredentialsValidator
) {
    operator fun invoke(email: String, password: String): AuthError? =
        validator.validate(email, password)
}
