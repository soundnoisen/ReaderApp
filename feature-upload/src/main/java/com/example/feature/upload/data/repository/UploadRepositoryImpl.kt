package com.example.feature.upload.data.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.feature.upload.data.worker.UploadBookWorker
import com.example.core.data.util.generatePdfCover
import com.example.core.domain.model.book.UploadError
import com.example.core.domain.model.book.UploadProgress
import com.example.feature.upload.data.util.copyToCache
import com.example.feature.upload.data.util.generateObjectKey
import com.example.feature.upload.domain.repository.UploadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UploadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): UploadRepository {

    override fun uploadBook(uri: Uri, title: String, author: String): Flow<UploadProgress> = flow {
        val file = copyToCache(context, uri)
        val coverPath = generatePdfCover(context, file)
        val objectKey = generateObjectKey(file)

        val data = workDataOf(
            UploadBookWorker.FILE_PATH to file.absolutePath,
            UploadBookWorker.COVER_PATH to coverPath,
            UploadBookWorker.OBJECT_KEY to objectKey,
            UploadBookWorker.TITLE to title,
            UploadBookWorker.AUTHOR to author
        )

        val request = OneTimeWorkRequestBuilder<UploadBookWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(request)

        WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(request.id)
            .asFlow()
            .collect { info ->
                info?.let {
                    when(it.state) {
                        WorkInfo.State.RUNNING -> {
                            emit(UploadProgress.Uploading(it.progress.getInt("progress", 0)))
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            it.outputData.getString("url")?.let { url -> emit(UploadProgress.Success(url)) }
                                ?: emit(UploadProgress.Error(UploadError.Unknown))
                        }
                        WorkInfo.State.FAILED -> {
                            emit(UploadProgress.Error(UploadError.Unknown))
                        }
                        else -> Unit
                    }
                }
            }
    }
}
