package com.example.feature.profile.domain.usecase

import com.example.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class LogoutProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
){
    operator fun invoke() = repository.logoutProfile()
}