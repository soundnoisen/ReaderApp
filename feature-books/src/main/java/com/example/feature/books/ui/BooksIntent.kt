package com.example.feature.books.ui

import com.example.core.domain.model.Book

sealed class BooksIntent {
    data class SearchQueryChanged(val query: String): BooksIntent()
    object SearchQueryClear: BooksIntent()
    object Refresh : BooksIntent()
    data class BookOpenClicked(val book: Book): BooksIntent()
    data class BookDeleteClicked(val book: Book): BooksIntent()
    data class BookDownloadClicked(val book: Book): BooksIntent()
    object RetryClicked: BooksIntent()
    object DeleteDismissed: BooksIntent()
    object LocallyDeleteConfirmed: BooksIntent()
    object EverywhereDeleteConfirmed: BooksIntent()
}