package com.example.feature.books.data.repository

import android.content.Context
import com.example.core.data.datasource.BookLocalDataSource
import com.example.core.data.mapper.toDomain
import com.example.core.data.util.generatePdfCover
import com.example.core.domain.model.Book
import com.example.core.domain.model.book.DeleteResult
import com.example.core.domain.model.book.DownloadError
import com.example.core.domain.model.book.DownloadProgress
import com.example.core.domain.repository.ReaderPositionRepository
import com.example.core.domain.repository.YandexStorageRepository
import com.example.feature.books.domain.repository.BookRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File

class BookRepositoryImpl @Inject constructor(
    private val storage: YandexStorageRepository,
    private val localDataSource: BookLocalDataSource,
    private val readerPosition: ReaderPositionRepository,
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
        try {
            val extension = book.fileUrl.substringAfterLast('.', "dat")
            val booksDir = File(context.filesDir, "books").apply { if (!exists()) mkdirs() }

            val localFile = File(booksDir, "${book.id}_${book.title}.$extension")

            storage.downloadFile(book.fileUrl, localFile)
                .catch { e -> emit(DownloadProgress.Error(DownloadError.Unknown(e.message.toString()))) }
                .collect { progress -> emit(progress) }

            if (!localFile.exists() || localFile.length() == 0L) {
                emit(DownloadProgress.Error(DownloadError.FileNotFound))
                return@flow
            }

            val coverPath = if (extension.lowercase() == "pdf") {
                generatePdfCover(context, localFile)
            } else null

            localDataSource.updateBook(
                bookId = book.id,
                localFilePath = localFile.absolutePath,
                readingProgress = book.readingProgress,
                coverPath = coverPath
            )
        } catch (e: Exception) {
            emit(DownloadProgress.Error(DownloadError.Unknown(e.message.toString())))
        }
    }

    override fun observeUserBooks(): Flow<List<Book>> {
        return localDataSource.observeBooks()
            .map { entities ->
                entities.map { entity ->
                   entity.toDomain()
                }
            }
    }

    override fun searchDownloadedBooks(query: String): Flow<List<Book>> {
        return localDataSource.searchDownloadedBooks(query)
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun validateLocalBooks() {
        val books = localDataSource.getAllBooksOnce()
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