package com.example.feature.books.domain.usecase

import com.example.core.domain.model.Book
import com.example.feature.books.domain.repository.BookRepository
import javax.inject.Inject

class LocallyDeleteBookUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    suspend operator fun invoke(book: Book) {
        repository.locallyDeleteBook(book)
    }
}