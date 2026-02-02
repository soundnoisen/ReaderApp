package com.example.feature.books.domain.usecase

import com.example.core.domain.model.Book
import com.example.core.domain.model.book.DeleteResult
import com.example.feature.books.domain.repository.BookRepository
import javax.inject.Inject

class EverywhereDeleteBookUseCase @Inject constructor(
    private val repository: BookRepository,
){
    suspend operator fun invoke(book: Book): DeleteResult {
        return repository.everywhereDeleteBook(book)
    }
}