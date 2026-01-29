package com.example.feature.book.reader.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.domain.model.reader.BookContent
import com.example.core.domain.model.reader.ReaderPosition
import com.example.core.ui.R
import com.example.feature.book.reader.ui.BookReaderState

@Composable
fun BookReaderContent(
    content: BookContent?,
    state: BookReaderState,
    listState: LazyListState,
    onPositionChanged: (ReaderPosition) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (content) {
            is BookContent.Text -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(content.content) { block ->
                        Text(
                            text = block,
                            fontSize = state.fontSizeSp.sp,
                            lineHeight = state.fontSizeSp.sp * (state.lineHeightPercent / 100f)
                        )
                    }
                }
            }

            is BookContent.Pdf -> {
                PdfViewer(
                    path = content.path,
                    modifier = Modifier.fillMaxSize(),
                    currentPage = state.position.anchorIndex,
                    onPageChange = { page ->
                        val position = ReaderPosition(anchorIndex = page, progressInItem = 0f)
                        onPositionChanged(position)
                    },
                )
            }

            is BookContent.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_warning),
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        contentDescription = null)
                }
            }

            null -> {}
        }
    }
}
