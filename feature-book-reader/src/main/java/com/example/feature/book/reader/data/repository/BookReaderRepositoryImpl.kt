package com.example.feature.book.reader.data.repository

import com.example.core.data.source.BookLocalDataSource
import com.example.core.data.mapper.toDomain
import com.example.core.domain.model.Book
import com.example.core.domain.model.reader.BookContent
import com.example.core.domain.repository.BookContentReaderRepository
import com.example.feature.book.reader.domain.repository.BookReaderRepository
import javax.inject.Inject

class BookReaderRepositoryImpl @Inject constructor(
    private val localDataSource: BookLocalDataSource,
    private val reader: BookContentReaderRepository
): BookReaderRepository {

    override suspend fun getBookById(bookId: String): Book {
        return localDataSource.getBook(bookId).toDomain()
    }

    override suspend fun getBookContent(localPath: String): BookContent {
        return reader.getBookContent(localPath)
    }

    override suspend fun updateBookReadingProgress(bookId: String, progress: Float) {
        localDataSource.updateProgress(bookId, progress)
    }
}