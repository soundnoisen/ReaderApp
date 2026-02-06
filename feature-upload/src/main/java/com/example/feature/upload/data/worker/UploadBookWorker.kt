package com.example.feature.upload.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.core.data.source.BookLocalDataSource
import com.example.core.domain.model.Book
import com.example.core.domain.model.book.UploadProgress
import com.example.core.domain.repository.FirebaseRepository
import com.example.core.domain.repository.YandexStorageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.util.UUID

@HiltWorker
class UploadBookWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: YandexStorageRepository,
    private val localDataSource: BookLocalDataSource,
    private val firebaseRepository: FirebaseRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val filePath = inputData.getString(UploadWorkerKeys.FILE_PATH) ?: return Result.failure()
        val coverPath = inputData.getString(UploadWorkerKeys.COVER_PATH)
        val objectKey = inputData.getString(UploadWorkerKeys.OBJECT_KEY) ?: return Result.failure()
        val title = inputData.getString(UploadWorkerKeys.TITLE) ?: return Result.failure()
        val author = inputData.getString(UploadWorkerKeys.AUTHOR) ?: return Result.failure()

        val file = File(filePath)
        if (!file.exists()) return Result.failure()

        try {
            var uploadedUrl: String? = null

            repository.uploadFile(file, objectKey).collect { progress ->
                when (progress) {
                    is UploadProgress.Uploading -> setProgress(workDataOf("progress" to progress.percent))
                    is UploadProgress.Success -> uploadedUrl = progress.url
                    else -> Unit
                }
            }

            localDataSource.insert(
                uid = firebaseRepository.requireCurrentUserId(),
                book = Book(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    author = author,
                    fileUrl = uploadedUrl!!,
                    localFilePath = file.absolutePath,
                    coverPath = coverPath,
                    readingProgress = 0f,
                )
            )

            return Result.success(workDataOf("url" to uploadedUrl))
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}