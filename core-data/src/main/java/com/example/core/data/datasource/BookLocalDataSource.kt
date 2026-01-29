package com.example.core.data.datasource

import com.example.core.data.local.room.BookDao
import com.example.core.data.local.room.BookEntity
import com.example.core.domain.model.Book
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class BookLocalDataSource @Inject constructor(
    private val dao: BookDao
) {

    suspend fun insert(book: Book) {
        val entity = BookEntity(
            id = book.id,
            title = book.title,
            author = book.author,
            fileUrl = book.fileUrl,
            localFilePath = book.localFilePath,
            coverPath = book.coverPath,
            readingProgress = book.readingProgress
        )
        dao.insert(entity)
    }

    suspend fun updateBook(
        bookId: String,
        localFilePath: String?,
        coverPath: String?,
        readingProgress: Float
    ) = dao.updateBook(bookId, localFilePath, coverPath, readingProgress)

    fun observeBooks(): Flow<List<BookEntity>> = dao.getAllBooks()

    suspend fun getAllBooksOnce(): List<BookEntity> = dao.getAllBooksOnce()

    suspend fun getBook(id: String): BookEntity = dao.getBookById(id)

    suspend fun delete(bookId: String) = dao.deleteBookById(bookId)

    fun searchDownloadedBooks(query: String): Flow<List<BookEntity>> = dao.searchDownloadedBooks(query)

    suspend fun updateProgress(bookId: String, progress: Float) = dao.updateReadingProgress(bookId, progress)

    suspend fun clear() = dao.clear()
}