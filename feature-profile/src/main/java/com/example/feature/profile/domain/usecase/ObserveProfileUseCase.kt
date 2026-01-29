package com.example.feature.profile.domain.usecase

import com.example.core.domain.model.User
import com.example.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    operator fun invoke(): Flow<User?> {
        return repository.observeProfile()
    }
}