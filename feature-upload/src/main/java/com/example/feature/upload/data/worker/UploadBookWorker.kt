package com.example.feature.upload.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.core.data.source.BookLocalDataSource
import com.example.core.data.util.NotificationUtils
import com.example.core.data.worker.BaseFileWorker
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
    private val storage: YandexStorageRepository,
    private val localDataSource: BookLocalDataSource,
    private val firebaseRepository: FirebaseRepository
): BaseFileWorker(context, params) {

    override suspend fun doWork(): Result {
        val filePath = inputData.getString(UploadBookWorkerKeys.FILE_PATH) ?: return Result.failure()
        val coverPath = inputData.getString(UploadBookWorkerKeys.COVER_PATH)
        val objectKey = inputData.getString(UploadBookWorkerKeys.OBJECT_KEY) ?: return Result.failure()
        val title = inputData.getString(UploadBookWorkerKeys.TITLE) ?: return Result.failure()
        val author = inputData.getString(UploadBookWorkerKeys.AUTHOR) ?: return Result.failure()

        val foregroundId = objectKey.hashCode()
        val resultId = "${objectKey}_result".hashCode()

        setForeground(createForegroundInfo(foregroundId, title))

        val file = File(filePath)
        if (!file.exists()) return Result.failure()

        try {
            var uploadedUrl: String? = null

            storage.uploadFile(file, objectKey).collect { progress ->
                when (progress) {
                    is UploadProgress.Uploading -> {
                        setProgress(workDataOf("progress" to progress.percent))
                        notificationManager.notify(foregroundId, NotificationUtils.buildProgressNotification(applicationContext, "Загрузка «$title» ", progress.percent))
                    }
                    is UploadProgress.Success -> uploadedUrl = progress.url
                    is UploadProgress.Error -> throw RuntimeException(progress.error.toString())
                    else -> Unit
                }
            }

            if (uploadedUrl == null) {
                notifyComplete(resultId, title, false)
                return Result.failure()
            }

            localDataSource.insert(
                uid = firebaseRepository.requireCurrentUserId(),
                book = Book(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    author = author,
                    fileUrl = uploadedUrl,
                    localFilePath = file.absolutePath,
                    coverPath = coverPath,
                    readingProgress = 0f,
                )
            )

            notifyComplete(resultId, title, true)
            return Result.success(workDataOf("url" to uploadedUrl))
        } catch (e: Exception) {
            notifyComplete(resultId, title, false)
            return Result.retry()
        }
    }
}