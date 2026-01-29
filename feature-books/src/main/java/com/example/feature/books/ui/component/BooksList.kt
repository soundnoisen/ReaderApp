package com.example.feature.books.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.Book

@Composable
fun BooksList(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    onActionClick: (Book) -> Unit
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
            count = sortedBooks.size,
            key = { index -> sortedBooks[index].id }
        ) { index ->
            (sortedBooks[index]).also {
                BookItem(
                    book = it,
                    isLocalFile = it.localFilePath != null ,
                    onClicked = { onBookClick(it) },
                    onActionClicked = { onActionClick(it) }
                )
            }
        }
    }
}

