package com.example.feature.books.domain.usecase

import com.example.feature.books.domain.repository.BookRepository
import jakarta.inject.Inject

class RefreshBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke() {
        repository.validateLocalBooks()
    }
}
