package com.example.core.domain.repository

import com.example.core.domain.model.book.DeleteResult
import com.example.core.domain.model.book.DownloadProgress
import com.example.core.domain.model.book.UploadProgress
import kotlinx.coroutines.flow.Flow
import java.io.File

interface YandexStorageRepository {
    suspend fun deleteFile(fileUrl: String): DeleteResult
    fun downloadFile(fileUrl: String, destinationFile: File): Flow<DownloadProgress>
    fun uploadFile(file: File, objectKey: String): Flow<UploadProgress>
}