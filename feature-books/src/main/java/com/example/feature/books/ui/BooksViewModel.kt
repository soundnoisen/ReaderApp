package com.example.feature.books.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.Book
import com.example.core.domain.model.book.DeleteError
import com.example.core.domain.model.book.DeleteResult
import com.example.core.domain.model.book.DownloadError
import com.example.core.domain.model.book.DownloadProgress
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.feature.books.domain.usecase.DownloadBookUseCase
import com.example.feature.books.domain.usecase.EverywhereDeleteBookUseCase
import com.example.feature.books.domain.usecase.LocallyDeleteBookUseCase
import com.example.feature.books.domain.usecase.ObserveUserBooksUseCase
import com.example.feature.books.domain.usecase.RefreshBooksUseCase
import com.example.feature.books.domain.usecase.SearchDownloadedBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BooksViewModel @Inject constructor(
    private val networkChecker: NetworkCheckerRepository,
    private val observeUserBooks: ObserveUserBooksUseCase,
    private val locallyDeleteBook: LocallyDeleteBookUseCase,
    private val everywhereDeleteBook: EverywhereDeleteBookUseCase,
    private val downloadBook: DownloadBookUseCase,
    private val refreshBooks: RefreshBooksUseCase,
    private val searchBooks: SearchDownloadedBooksUseCase
): ViewModel() {

    private val _state = MutableStateFlow(BooksState())
    val state: StateFlow<BooksState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BooksEffect>()
    val effect: SharedFlow<BooksEffect> = _effect.asSharedFlow()

    init {
        observeBooks()
    }

    fun handleIntent(intent: BooksIntent) {
        when (intent) {
            is BooksIntent.BookDeleteClicked -> deleteBook(intent.book)
            is BooksIntent.BookDownloadClicked -> download(intent.book)
            is BooksIntent.BookOpenClicked -> openBook(intent.book)
            is BooksIntent.SearchQueryChanged -> searchQueryChanged(intent.query)
            is BooksIntent.SearchQueryClear -> searchQueryChanged("")
            is BooksIntent.Refresh -> refresh()
            is BooksIntent.RetryClicked -> retryDownload()
            is BooksIntent.DeleteDismissed -> deleteDismissed()
            is BooksIntent.EverywhereDeleteConfirmed -> everywhereDeleteConfirmed()
            is BooksIntent.LocallyDeleteConfirmed -> locallyDeleteConfirmed()
        }
    }

    private fun retryDownload() {
        _state.value.selectedBook?.let { download(it) }
    }

    private fun everywhereDeleteConfirmed() {
        performDelete(
            deleteType = { everywhereDeleteBook(it) }
        )
    }

    private fun locallyDeleteConfirmed() {
        performDelete(
            deleteType = { book ->
                locallyDeleteBook(book)
                null
            }
        )
    }

    private fun performDelete(
        deleteType: suspend (Book) -> DeleteResult?
    ) {
        val book = _state.value.selectedBook ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                deleteType(book)?.let { result ->
                    when (result) {
                        is DeleteResult.Error -> emitDeleteError(result.error)
                        is DeleteResult.Success -> sendEffect(BooksEffect.DeleteBookSuccessToast)
                    }
                } ?: run {
                    sendEffect(BooksEffect.DeleteBookSuccessToast)
                }
            } finally {
                _state.update { it.copy(isLoading = false, isDeleteDialogVisible = false) }
            }
        }
    }

    private fun deleteDismissed() {
        _state.update { it.copy(selectedBook = null, isDeleteDialogVisible = false) }
    }

    private fun openBook(book: Book) {
        if (book.localFilePath != null) sendEffect(BooksEffect.OpenBook(book))
        else emitDownloadError(DownloadError.FileNotFound)
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                refreshBooks()
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun searchQueryChanged(query: String) {
        _state.update { it.copy( query = query ) }
    }

    private fun deleteBook(book: Book) {
        _state.update { it.copy(selectedBook = book, isDeleteDialogVisible = true) }
    }

    private fun download(book: Book) {
        _state.update { it.copy(selectedBook = book) }

        if (!networkChecker.isNetworkAvailable())
            return emitDownloadError(DownloadError.Network, true)

        sendEffect(BooksEffect.RequestNotificationPermission)

        viewModelScope.launch {
            downloadBook(book).collect { progress ->
                updateBookState(book.id) { it.copy(downloadProgress = progress) }
                when (progress) {
                    is DownloadProgress.Success -> sendEffect(BooksEffect.DownloadBookSuccessToast)
                    is DownloadProgress.Error -> emitDownloadError(progress.error)
                    else -> Unit
                }
            }
        }
    }

    private fun updateBookState(bookId: String, update: (Book) -> Book) {
        _state.update { state ->
            state.copy(
                books = state.books.map { if (it.id == bookId) update(it) else it }
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeBooks() {
        state
            .map { it.query }
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                val booksFlow = if (query.isBlank()) {
                    observeUserBooks()
                } else {
                    searchBooks(query)
                }
                booksFlow
                    .onStart { _state.update { it.copy(isLoading = true) } }
                    .onEach { _state.update { it.copy(isLoading = false) } }
            }
            .onEach { books ->
                _state.update { it.copy(books = books) }
            }
            .launchIn(viewModelScope)
    }

    private fun emitDownloadError(error: DownloadError, canRetry: Boolean = false) {
        sendEffect(BooksEffect.ShowDownloadError(error, canRetry))
    }

    private fun emitDeleteError(error: DeleteError) {
        sendEffect(BooksEffect.ShowDeleteError(error))
    }

    private fun sendEffect(effect: BooksEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}