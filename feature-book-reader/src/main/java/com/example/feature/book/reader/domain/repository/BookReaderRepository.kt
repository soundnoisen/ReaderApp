package com.example.feature.book.reader.domain.repository


import com.example.core.domain.model.Book
import com.example.core.domain.model.reader.BookContent

interface BookReaderRepository {
    suspend fun getBookById(bookId: String): Book
    suspend fun getBookContent(localPath: String): BookContent
    suspend fun updateBookReadingProgress(bookId: String, progress: Float)
}