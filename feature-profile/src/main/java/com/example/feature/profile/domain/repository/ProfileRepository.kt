package com.example.feature.profile.domain.repository

import android.net.Uri
import com.example.core.domain.model.User
import com.example.core.domain.model.profile.UpdateProfileResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<User?>
    suspend fun updateProfile(name: String?, photoUri: Uri?): UpdateProfileResult
    suspend fun logoutProfile()
}