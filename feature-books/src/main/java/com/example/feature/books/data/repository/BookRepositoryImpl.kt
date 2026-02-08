package com.example.feature.books.data.repository

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.core.data.mapper.toDomain
import com.example.core.data.source.BookLocalDataSource
import com.example.core.domain.model.Book
import com.example.core.domain.model.book.DeleteResult
import com.example.core.domain.model.book.DownloadError
import com.example.core.domain.model.book.DownloadProgress
import com.example.core.domain.repository.FirebaseRepository
import com.example.core.domain.repository.ReaderPositionRepository
import com.example.core.domain.repository.YandexStorageRepository
import com.example.feature.books.data.worker.DownloadBookWorker
import com.example.feature.books.data.worker.DownloadBookWorkerKeys
import com.example.feature.books.domain.repository.BookRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File

class BookRepositoryImpl @Inject constructor(
    private val storage: YandexStorageRepository,
    private val localDataSource: BookLocalDataSource,
    private val readerPosition: ReaderPositionRepository,
    private val firebaseRepository: FirebaseRepository,
    @ApplicationContext private val context: Context
): BookRepository {

    override suspend fun locallyDeleteBook(book: Book) {
        deleteFiles(book)
        localDataSource.updateBook(
            bookId = book.id,
            readingProgress = book.readingProgress,
            localFilePath = null,
            coverPath = null,
        )
    }

    fun deleteFiles(book: Book) {
        book.localFilePath?.let { path ->
            val file = File(path)
            if (file.exists()) file.delete()
        }
        book.coverPath?.let { path ->
            val file = File(path)
            if (file.exists()) file.delete()
        }
    }

    override suspend fun everywhereDeleteBook(book: Book): DeleteResult {
        val result = storage.deleteFile(book.fileUrl)
        if (result is DeleteResult.Success) {
            localDataSource.delete(book.id)
            deleteFiles(book)
            readerPosition.deletePosition(book.id)
        }
        return result
    }

    override fun downloadBook(book: Book): Flow<DownloadProgress> = flow {
        val extension = book.fileUrl.substringAfterLast('.', "dat")

        val data = workDataOf(
            DownloadBookWorkerKeys.ID to book.id,
            DownloadBookWorkerKeys.TITLE to book.title,
            DownloadBookWorkerKeys.EXTENSION to extension,
            DownloadBookWorkerKeys.URL to book.fileUrl,
            DownloadBookWorkerKeys.READING_PROGRESS to book.readingProgress
        )

        val request = OneTimeWorkRequestBuilder<DownloadBookWorker>()
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
                            emit(DownloadProgress.Downloading(it.progress.getInt("progress", 0)))
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            emit(DownloadProgress.Success)
                        }
                        WorkInfo.State.FAILED -> {
                            emit(DownloadProgress.Error(DownloadError.Unknown))
                        }
                        else -> Unit
                    }
                }
            }
    }

    override fun observeUserBooks(): Flow<List<Book>> {
        return localDataSource.observeBooks(firebaseRepository.requireCurrentUserId())
            .map { entities ->
                entities.map { entity ->
                   entity.toDomain()
                }
            }
    }

    override fun searchDownloadedBooks(query: String): Flow<List<Book>> {
        return localDataSource.searchDownloadedBooks(firebaseRepository.requireCurrentUserId(), query)
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun validateLocalBooks() {
        val books = localDataSource.getAllBooksOnce(firebaseRepository.requireCurrentUserId())
        books.forEach { entity ->
            val bookFile = entity.localFilePath?.let { File(it) }
            val coverFile = entity.coverPath?.let { File(it) }
            if (bookFile == null || !bookFile.exists()) {
                coverFile?.takeIf { it.exists() }?.delete()
                localDataSource.updateBook(
                    bookId = entity.id,
                    localFilePath = null,
                    coverPath = null,
                    readingProgress = 0f
                )
            }
        }
    }
}