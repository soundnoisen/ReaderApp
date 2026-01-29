package com.example.feature.books.domain.usecase

import com.example.core.domain.model.Book
import com.example.feature.books.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchDownloadedBooksUseCase @Inject constructor(
    private val repository: BookRepository
){
    operator fun invoke(query: String): Flow<List<Book>> {
        return repository.searchDownloadedBooks(query)
    }
}