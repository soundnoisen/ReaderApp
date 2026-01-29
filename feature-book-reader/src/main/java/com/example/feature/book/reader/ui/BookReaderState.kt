package com.example.feature.book.reader.ui


import com.example.core.domain.model.reader.BookContent
import com.example.core.domain.model.reader.ReaderPosition

data class BookReaderState(
    val id: String = "",
    val title: String = "",
    val isLoading: Boolean = false,
    val content: BookContent? = null,
    val position: ReaderPosition = ReaderPosition(),
    val isSettingsVisible: Boolean = false,
    val fontSizeSp: Int = 16,
    val lineHeightPercent: Int = 150,
)