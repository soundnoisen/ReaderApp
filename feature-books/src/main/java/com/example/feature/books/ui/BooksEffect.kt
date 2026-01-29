package com.example.feature.books.ui

import com.example.core.domain.model.Book
import com.example.core.domain.model.book.DownloadError

sealed class BooksEffect {
    data class OpenBook(val book: Book): BooksEffect()
    data class ShowError(val error: DownloadError, val canRetry: Boolean = false): BooksEffect()
    object DownloadBookSuccessToast: BooksEffect()
    object DeleteBookSuccessToast: BooksEffect()
}