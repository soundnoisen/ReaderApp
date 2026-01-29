package com.example.feature.book.reader.ui

import com.example.core.domain.model.reader.ReaderPosition

sealed class BookReaderIntent {
    object BackClicked: BookReaderIntent()
    data class SettingsClicked(val isSettingsVisible: Boolean): BookReaderIntent()
    object SettingsDismisses: BookReaderIntent()
    object FontSizeInc: BookReaderIntent()
    object FontSizeDec: BookReaderIntent()
    object LineHeightInc: BookReaderIntent()
    object LineHeightDec: BookReaderIntent()
    data class ThemeChanged(val isDarkTheme: Boolean): BookReaderIntent()
    data class UpdatePosition(val position: ReaderPosition): BookReaderIntent()
}
