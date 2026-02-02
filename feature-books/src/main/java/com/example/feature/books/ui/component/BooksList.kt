package com.example.feature.books.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.Book
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState

@Composable
fun BooksList(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    onActionClick: (Book) -> Unit,
    onDelete: (Book) -> Unit
) {

    val sortedBooks = remember(books) {
        books.sortedByDescending { it.localFilePath != null }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = sortedBooks,
            key = { it.id }
        ) { book ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(book)
                    }
                    false
                }
            )
            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = { DeleteBackground(dismissState) },
                enableDismissFromStartToEnd = false
            ) {
                AnimatedVisibility(
                    visible = dismissState.currentValue == SwipeToDismissBoxValue.Settled,
                    exit = shrinkVertically() + fadeOut()
                ) {
                    BookItem(
                        book = book,
                        isLocalFile = book.localFilePath != null,
                        onClicked = { onBookClick(book) },
                        onActionClicked = { onActionClick(book) }
                    )
                }
            }
        }
    }
}

