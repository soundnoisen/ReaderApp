package com.example.feature.upload.data.repository

import android.content.Context
import android.net.Uri
import com.example.core.data.source.BookLocalDataSource
import com.example.core.data.util.generatePdfCover
import com.example.core.domain.model.Book
import com.example.core.domain.model.book.UploadProgress
import com.example.core.domain.repository.YandexStorageRepository
import com.example.feature.upload.BuildConfig
import com.example.feature.upload.data.util.copyToCache
import com.example.feature.upload.domain.repository.UploadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class UploadRepositoryImpl @Inject constructor(
    private val storage: YandexStorageRepository,
    private val localDataSource: BookLocalDataSource,
    @ApplicationContext private val context: Context
): UploadRepository {

    override fun uploadBook(uri: Uri, title: String, author: String): Flow<UploadProgress> = flow {
        val file = copyToCache(context, uri)
        val coverPath = generatePdfCover(context, file)
        val objectKey = "books/${UUID.randomUUID()}_${file.name}"

        storage.uploadFile(file, BuildConfig.YANDEX_BUCKET, objectKey).collect { progress ->
            if (progress is UploadProgress.Success) {
                localDataSource.insert(
                    Book(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        author = author,
                        fileUrl = progress.url,
                        localFilePath = file.absolutePath,
                        coverPath = coverPath,
                        readingProgress = 0f
                    )
                )
            }
            emit(progress)
        }
    }
}
