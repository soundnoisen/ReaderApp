package com.example.feature.books.ui

import com.example.core.domain.model.Book

data class BooksState(
    val selectedBook: Book? = null,
    val books: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val query: String = "",
    val isDeleteDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)