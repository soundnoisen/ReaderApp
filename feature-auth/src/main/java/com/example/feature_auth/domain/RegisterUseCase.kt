package com.example.feature_auth.domain

import com.example.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    suspend operator fun invoke(email: String, password: String) = repository.register(email, password)
}