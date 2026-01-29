package com.example.feature.profile.domain.usecase

import android.net.Uri
import com.example.core.domain.model.profile.UpdateProfileResult
import com.example.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(name: String?, photoUri: Uri?): UpdateProfileResult {
        return repository.updateProfile(name, photoUri)
    }
}