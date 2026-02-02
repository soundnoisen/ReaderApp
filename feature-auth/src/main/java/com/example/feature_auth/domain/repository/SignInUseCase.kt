package com.example.feature_auth.domain.repository

import com.example.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend operator fun invoke(email: String, password: String) = repository.signIn(email, password)
}
