package com.example.feature.books.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.core.data.source.BookLocalDataSource
import com.example.core.data.util.NotificationUtils
import com.example.core.data.util.generatePdfCover
import com.example.core.domain.model.book.DownloadProgress
import com.example.core.domain.repository.YandexStorageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import com.example.core.data.worker.BaseFileWorker


@HiltWorker
class DownloadBookWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val storage: YandexStorageRepository,
    private val localDataSource: BookLocalDataSource
): BaseFileWorker(context, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getString(ID) ?: return Result.failure()
        val title = inputData.getString(TITLE) ?: return Result.failure()
        val extension = inputData.getString(EXTENSION) ?: return Result.failure()
        val url = inputData.getString(URL) ?: return Result.failure()
        val readingProgress = inputData.getFloat(READING_PROGRESS, 0f)

        val foregroundId = id.hashCode()
        val resultId = "${id}_result".hashCode()

        setForeground(createForegroundInfo(foregroundId, title))

        val booksDir = File(applicationContext.filesDir, "books").apply { if (!exists()) mkdirs() }
        val localFile = File(booksDir, "${id}_${title}.$extension")

        try {

            storage.downloadFile(url, localFile).collect { progress ->
                when (progress) {
                    is DownloadProgress.Downloading -> {
                        setProgress(workDataOf("progress" to progress.percent))
                        notificationManager.notify(foregroundId, NotificationUtils.buildProgressNotification(applicationContext, "Загрузка «$title» ", progress.percent))
                    }
                    is DownloadProgress.Error -> throw RuntimeException(progress.error.toString())
                    else -> Unit
                }
            }

            if (!localFile.exists() || localFile.length() == 0L) {
                notifyComplete(resultId, title, false)
                return Result.failure()
            }

            val coverPath = if (extension.lowercase() == "pdf") {
                generatePdfCover(applicationContext, localFile)
            } else null

            localDataSource.updateBook(
                bookId = id,
                localFilePath = localFile.absolutePath,
                readingProgress = readingProgress,
                coverPath = coverPath
            )

            notifyComplete(resultId, title, true)
            return Result.success()
        } catch (e: Exception) {
            notifyComplete(resultId, title, false)
            return Result.retry()
        }
    }

    companion object {
        const val ID = "id"
        const val TITLE = "title"
        const val EXTENSION = "extension"
        const val URL = "url"
        const val READING_PROGRESS = "reading_progress"
    }
}