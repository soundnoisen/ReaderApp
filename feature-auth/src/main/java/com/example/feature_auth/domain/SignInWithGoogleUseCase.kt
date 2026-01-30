package com.example.feature_auth.domain

import com.example.core.domain.model.auth.AuthResult
import com.example.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend operator fun invoke(): AuthResult {
        return repository.signInWithGoogle()
    }
}