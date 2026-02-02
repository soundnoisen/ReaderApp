package com.example.feature_auth.domain.repository

import com.example.core.domain.repository.FirebaseRepository
import jakarta.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    operator fun invoke() = repository.currentUser()
}