package com.example.feature.books.domain.usecase

import com.example.core.domain.model.Book
import com.example.core.domain.model.book.DownloadProgress
import com.example.feature.books.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DownloadBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(book: Book): Flow<DownloadProgress> {
        return repository.downloadBook(book)
    }
}