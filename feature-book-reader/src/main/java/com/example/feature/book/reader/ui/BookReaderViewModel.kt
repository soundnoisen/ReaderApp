package com.example.feature.book.reader.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.reader.BookContent
import com.example.core.domain.model.reader.BookContentError
import com.example.core.domain.model.reader.ReaderPosition
import com.example.core.domain.model.reader.fullProgress
import com.example.core.domain.usecase.GetBookReaderPositionUseCase
import com.example.core.domain.usecase.SetBookReaderPositionUseCase
import com.example.feature.book.reader.domain.usecase.GetBookByIdUseCase
import com.example.feature.book.reader.domain.usecase.GetBookContentUseCase
import com.example.feature.book.reader.domain.usecase.UpdateBookReadingProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBookById: GetBookByIdUseCase,
    private val getBookContent: GetBookContentUseCase,
    private val getBookReaderPosition: GetBookReaderPositionUseCase,
    private val setBookReaderPosition: SetBookReaderPositionUseCase,
    private val updateBookReadingProgress: UpdateBookReadingProgressUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(BookReaderState())
    val state: StateFlow<BookReaderState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BookReaderEffect>()
    val effect: SharedFlow<BookReaderEffect> = _effect.asSharedFlow()

    private val bookId: String = requireNotNull(savedStateHandle["bookId"])

    init {
        loadBook()
    }

    fun handleIntent(intent: BookReaderIntent) {
        when (intent) {
            is BookReaderIntent.BackClicked -> navigateBack()
            is BookReaderIntent.SettingsClicked -> settingsClicked(intent.isSettingsVisible)
            is BookReaderIntent.SettingsDismisses -> settingsDismisses()
            is BookReaderIntent.ThemeChanged -> themeChanged(intent.isDarkTheme)
            is BookReaderIntent.FontSizeDec -> updateFontSize { it - 2 }
            is BookReaderIntent.FontSizeInc -> updateFontSize { it + 2 }
            is BookReaderIntent.LineHeightDec -> updateLineHeight { it - 10 }
            is BookReaderIntent.LineHeightInc -> updateLineHeight { it + 10 }
            is BookReaderIntent.UpdatePosition -> updatePosition(intent.position)
        }
    }

    private fun updatePosition(position: ReaderPosition) {
        viewModelScope.launch {
            _state.update { it.copy(position = position) }
            setBookReaderPosition(bookId, position)
        }
    }

    private fun updateLineHeight(change:(Int) -> Int) {
        _state.update { it.copy(lineHeightPercent = change(it.lineHeightPercent).coerceIn(100,200)) }
    }

    private fun updateFontSize(change:(Int) -> Int) {
        _state.update { it.copy(fontSizeSp = change(it.fontSizeSp).coerceIn(12,26)) }
    }

    private fun themeChanged(isDarkTheme: Boolean) {
        sendEffect(BookReaderEffect.ThemeChange(isDarkTheme))
    }

    private fun settingsDismisses() {
        _state.update { it.copy(isSettingsVisible = false) }
    }

    private fun settingsClicked(isSettingsVisible: Boolean) {
        _state.update { it.copy(isSettingsVisible = isSettingsVisible) }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            val progress = _state.value.position.fullProgress(_state.value.content)
            updateBookReadingProgress(bookId, progress)
            sendEffect(BookReaderEffect.NavigateBack)
        }
    }

    private fun loadBook() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val book = getBookById(bookId)
                _state.update { it.copy(title = book.title) }

                getBookReaderPosition(bookId)?.let { position -> _state.update { it.copy(position = position) } }

                val content = book.localFilePath?.let { getBookContent(it) }
                    ?: return@launch emitError(BookContentError.FileNotFound)

                when (content) {
                    is BookContent.Text -> _state.update { it.copy(content = BookContent.Text(content.content)) }
                    is BookContent.Pdf -> _state.update { it.copy(content = BookContent.Pdf(content.path, content.pageCount, content.startPage)) }
                    is BookContent.Error -> emitError(content.error)
                }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun emitError(error: BookContentError) {
        sendEffect(BookReaderEffect.ShowError(error))
    }

    private fun sendEffect(effect: BookReaderEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}