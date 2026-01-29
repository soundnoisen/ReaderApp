package com.example.feature.upload.domain.usecase

import android.net.Uri
import com.example.core.domain.model.book.UploadProgress
import com.example.feature.upload.domain.repository.UploadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val repository: UploadRepository
) {
    operator fun invoke(uri: Uri, title: String, author: String): Flow<UploadProgress> {
        return repository.uploadBook(uri, title, author)
    }
}