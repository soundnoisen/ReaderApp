package com.example.feature.profile.data.repository

import android.net.Uri
import com.example.core.domain.model.User
import com.example.core.domain.model.profile.UpdateProfileError
import com.example.core.domain.model.profile.UpdateProfileResult
import com.example.core.domain.repository.CloudinaryRepository
import com.example.core.domain.repository.FirebaseRepository
import com.example.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val cloudinaryRepository: CloudinaryRepository
): ProfileRepository {

    override fun observeProfile(): Flow<User?> = firebaseRepository.observeUser()

    override suspend fun updateProfile(name: String?, photoUri: Uri?): UpdateProfileResult {

        if (!firebaseRepository.userAuthorized()) {
            return UpdateProfileResult.Error(UpdateProfileError.UserIsNotAuthorized)
        }

        var photoUrl: String? = null

        if (photoUri != null) {
            when (val result = cloudinaryRepository.uploadPhoto(photoUri)) {
                is UpdateProfileResult.Error -> return result
                is UpdateProfileResult.Success -> photoUrl = result.url
            }
        }

        return firebaseRepository.updateProfile(name, photoUrl)
    }

    override suspend fun logoutProfile() {
        firebaseRepository.logout()
    }
}