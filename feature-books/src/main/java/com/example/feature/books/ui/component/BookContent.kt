package com.example.feature.books.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.Book
import com.example.core.domain.model.book.DownloadProgress
import com.example.feature.books.R

@Composable
fun BookContent(
    book: Book,
    onClick: () -> Unit,
    isLocalFile: Boolean,
    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            BookTitle(text = book.title, isLocalFile)
            Spacer(Modifier.height(8.dp))
            BookAuthorText(text = book.author, isLocalFile)
            BookReadingIndicator(isLocalFile, book.readingProgress
            )
        }

        Spacer(Modifier.width(16.dp))

        if (book.downloadProgress !is DownloadProgress.Downloading) {
            IconButton(
                onClick = onClick,
                Modifier.size(40.dp)
            ) {
                Icon(
                    painter = if (isLocalFile)
                        painterResource(R.drawable.ic_delete)
                    else
                        painterResource(R.drawable.ic_download),
                    contentDescription = null,
                    tint = if (isLocalFile) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onBackground.copy(0.8f)
                )
            }
        } else {
            val progressPercent = (book.downloadProgress as DownloadProgress.Downloading).percent.toFloat() / 100f
            Box(Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progressPercent,
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
