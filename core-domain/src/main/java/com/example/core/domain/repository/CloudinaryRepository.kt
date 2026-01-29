package com.example.core.domain.repository

import android.net.Uri
import com.example.core.domain.model.profile.UpdateProfileResult

interface CloudinaryRepository {
    suspend fun uploadPhoto(uri: Uri): UpdateProfileResult
}