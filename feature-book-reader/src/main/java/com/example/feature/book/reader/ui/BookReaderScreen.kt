package com.example.feature.book.reader.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.core.domain.model.reader.BookContent
import com.example.core.domain.model.reader.ReaderPosition
import com.example.core.domain.model.reader.fullProgress
import com.example.core.ui.component.BaseSnackBar
import com.example.feature.book.reader.ui.component.BookReaderContent
import com.example.feature.book.reader.ui.component.BookReaderProgress
import com.example.feature.book.reader.ui.component.BookReaderTopBar
import com.example.feature.book.reader.ui.component.ReaderSettingsBottomSheet
import com.example.feature.book.reader.ui.mapper.toUiText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderScreen(
    navigateBack: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    viewModel: BookReaderViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val textContent = (state.content as? BookContent.Text)?.content.orEmpty()
    var restoredPosition by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    val content = LocalContext.current

    // Восстановление позиции текста
    LaunchedEffect(state.content, state.fontSizeSp, state.lineHeightPercent) {
        val lastIndex = textContent.lastIndex
        if (lastIndex >= 0 && !restoredPosition) {
            val anchor = state.position.anchorIndex.coerceIn(0, lastIndex)
            val itemSize = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == anchor }?.size ?: 0
            val offset = (itemSize * state.position.progressInItem).toInt()
            listState.scrollToItem(anchor, offset)
            restoredPosition = true
        }
    }

    // Слежение за прокруткой текста
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .drop(1)
            .collect { (index, offset) ->
                val itemSize = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }?.size ?: 1
                val progress = (offset.toFloat() / itemSize.toFloat()).coerceIn(0f, 1f)
                val position = ReaderPosition(anchorIndex = index, progressInItem = progress)
                viewModel.handleIntent(BookReaderIntent.UpdatePosition(position))
            }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is BookReaderEffect.NavigateBack -> navigateBack()
                is BookReaderEffect.ShowError -> snackBarHostState.showSnackbar(effect.error.toUiText(content.resources))
                is BookReaderEffect.ThemeChange -> onThemeChange(effect.isDarkTheme)
            }
        }
    }

    Scaffold(
        snackbarHost = {
            BaseSnackBar(
                snackBarHostState = snackBarHostState,
            )
        },
        topBar = {
            Column(
                modifier = Modifier.padding(top = 18.dp)
            ) {
                BookReaderTopBar(
                    title = state.title,
                    isSettingsVisible = state.isSettingsVisible,
                    onBack = { viewModel.handleIntent(BookReaderIntent.BackClicked) },
                    onSettingsClick = { viewModel.handleIntent(BookReaderIntent.SettingsClicked(it)) }
                )
                Spacer(Modifier.height(8.dp))
                BookReaderProgress(
                    progress = state.position.fullProgress(state.content)
                )
            }
        },
        contentWindowInsets = WindowInsets(bottom = 0.dp),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BookReaderContent(
                content = state.content,
                state = state,
                listState = listState,
                onPositionChanged = { position -> viewModel.handleIntent(BookReaderIntent.UpdatePosition(position)) },
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            )

            if (state.isSettingsVisible) {
                ReaderSettingsBottomSheet(
                    settings = state,
                    isDarkTheme = isDarkTheme,
                    onFontDec = { viewModel.handleIntent(BookReaderIntent.FontSizeDec) },
                    onFontInc = { viewModel.handleIntent(BookReaderIntent.FontSizeInc) },
                    onLineDec = { viewModel.handleIntent(BookReaderIntent.LineHeightDec) },
                    onLineInc = { viewModel.handleIntent(BookReaderIntent.LineHeightInc) },
                    onThemeChanged = { viewModel.handleIntent(BookReaderIntent.ThemeChanged(it)) },
                    onDismiss = { viewModel.handleIntent(BookReaderIntent.SettingsDismisses) }
                )
            }
        }
    }
}