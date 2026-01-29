package com.example.feature.book.reader.domain.usecase

import com.example.core.domain.model.Book
import com.example.feature.book.reader.domain.repository.BookReaderRepository
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val repository: BookReaderRepository
) {
    suspend operator fun invoke(bookId: String): Book {
        return repository.getBookById(bookId)
    }
}