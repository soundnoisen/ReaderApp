package com.example.feature.books.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.core.ui.component.BaseHeader
import com.example.core.ui.component.BaseSnackBar
import com.example.core.ui.component.ReaderIcon
import com.example.feature.books.R
import com.example.feature.books.ui.component.BooksList
import com.example.feature.books.ui.component.DeleteAlertDialog
import com.example.feature.books.ui.component.ListInformationText
import com.example.feature.books.ui.component.SearchTextField
import com.example.feature.books.ui.mapper.toUiText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(
    viewModel: BooksViewModel = hiltViewModel(),
    navigateToBook: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var currentEffect by remember { mutableStateOf<BooksEffect?>(null) }
    val snackBarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            currentEffect = effect
            when(effect) {
                is BooksEffect.OpenBook -> navigateToBook(effect.book.id)
                is BooksEffect.DownloadBookSuccessToast -> Toast.makeText(context, context.resources.getString(R.string.msg_download_book_success), Toast.LENGTH_SHORT).show()
                is BooksEffect.DeleteBookSuccessToast -> Toast.makeText(context, context.resources.getString(R.string.msg_delete_book_success), Toast.LENGTH_SHORT).show()
                is BooksEffect.ShowDeleteError -> snackBarHostState.showSnackbar(effect.error.toUiText(context.resources))
                is BooksEffect.ShowDownloadError -> snackBarHostState.showSnackbar(effect.error.toUiText(context.resources))
            }
        }
    }

    Scaffold(
        snackbarHost = {
            BaseSnackBar(
                snackBarHostState = snackBarHostState,
                canRetry = currentEffect?.let { it is BooksEffect.ShowDownloadError && it.canRetry } ?: false,
                onRetry = { viewModel.handleIntent(BooksIntent.RetryClicked) }
            )
        },
        topBar = { BaseHeader(
            text = stringResource(R.string.title_books),
            modifier =  Modifier.fillMaxWidth().padding(top = 30.dp))
        },
        contentWindowInsets = WindowInsets(bottom = 0.dp),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            SearchTextField(
                value = state.query,
                onValueChange = {
                    viewModel.handleIntent(BooksIntent.SearchQueryChanged(it)) },
                onClearClicked = {viewModel.handleIntent(BooksIntent.SearchQueryClear) },
                clearVisibility = state.query.isNotBlank(),
                placeholder = stringResource(R.string.placeholder_search_books),
            )

            when {
                !state.isLoading && state.books.isEmpty() && state.query.isBlank() -> {
                    ReaderIcon(painterResource(R.drawable.ic_reader))
                }

                !state.isLoading && state.books.isEmpty() && state.query.isNotBlank() -> {
                    ListInformationText(stringResource(R.string.msg_nothing_found))
                }

                else -> {
                    PullToRefreshBox(
                        isRefreshing = state.isLoading,
                        onRefresh = { viewModel.handleIntent(BooksIntent.Refresh) },
                        state = pullRefreshState,
                        indicator = {
                            Indicator(
                                modifier = Modifier.align(Alignment.TopCenter),
                                isRefreshing = state.isLoading,
                                containerColor = MaterialTheme.colorScheme.background,
                                color = MaterialTheme.colorScheme.primary,
                                state = pullRefreshState
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        BooksList(
                            books = state.books,
                            onBookClick = { book -> viewModel.handleIntent(BooksIntent.BookOpenClicked(book)) },
                            onActionClick = { book ->
                                if (book.localFilePath == null) {
                                    viewModel.handleIntent(BooksIntent.BookDownloadClicked(book))
                                } else {
                                    viewModel.handleIntent(BooksIntent.BookDeleteClicked(book))
                                }
                            },
                            onDelete = { book -> viewModel.handleIntent(BooksIntent.BookDeleteClicked(book)) }
                        )
                    }
                }
            }
            if (state.isDeleteDialogVisible) {
                DeleteAlertDialog(
                    dismissText = stringResource(R.string.action_cancel),
                    locallyConfirmText = stringResource(R.string.action_locally_delete),
                    everywhereConfirmText = stringResource(R.string.action_everywhere_delete),
                    onDismiss = { viewModel.handleIntent(BooksIntent.DeleteDismissed) },
                    onLocallyConfirm = { viewModel.handleIntent(BooksIntent.LocallyDeleteConfirmed) },
                    onEverywhereConfirm = { viewModel.handleIntent(BooksIntent.EverywhereDeleteConfirmed) },
                    title = stringResource(R.string.label_delete),
                    description = stringResource(R.string.msg_delete_confirmation) + " «${state.selectedBook?.title}»"
                )
            }
        }
    }
}

