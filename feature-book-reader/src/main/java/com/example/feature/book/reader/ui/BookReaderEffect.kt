package com.example.feature.book.reader.ui

import com.example.core.domain.model.reader.BookContentError

sealed class BookReaderEffect {
    object NavigateBack : BookReaderEffect()
    data class ShowError(val error: BookContentError): BookReaderEffect()
    data class ThemeChange(val isDarkTheme: Boolean): BookReaderEffect()
}