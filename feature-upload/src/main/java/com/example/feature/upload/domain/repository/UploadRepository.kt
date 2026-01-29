package com.example.feature.upload.domain.repository

import android.net.Uri
import com.example.core.domain.model.book.UploadProgress
import kotlinx.coroutines.flow.Flow

interface UploadRepository {
    fun uploadBook(uri: Uri, title: String, author: String): Flow<UploadProgress>
}
