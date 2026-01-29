package com.example.feature.books.domain.repository

import com.example.core.domain.model.Book
import com.example.core.domain.model.book.DeleteResult
import com.example.core.domain.model.book.DownloadProgress
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun locallyDeleteBook(book: Book)
    suspend fun everywhereDeleteBook(book: Book): DeleteResult
    fun downloadBook(book: Book): Flow<DownloadProgress>
    fun observeUserBooks(): Flow<List<Book>>
    fun searchDownloadedBooks(query: String): Flow<List<Book>>
    suspend fun validateLocalBooks()
}