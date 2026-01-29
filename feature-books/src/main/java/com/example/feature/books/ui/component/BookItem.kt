package com.example.feature.books.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.Book


@Composable
fun BookItem(
    modifier: Modifier = Modifier,
    book: Book,
    isLocalFile: Boolean,
    onClicked: () -> Unit,
    onActionClicked: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocalFile) MaterialTheme.colorScheme.primaryContainer  else MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        onClick = onClicked
    ) {
        Row(Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(16.dp)) {
            Box(Modifier
                .fillMaxHeight()
                .width(80.dp)
                .border(1.dp, MaterialTheme.colorScheme.onSecondaryContainer.copy(if (isLocalFile) 0.5f else 0.25f), RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
            ) {
                BookCover(coverPath = book.coverPath, isLocalFile = isLocalFile)
            }
            Spacer(Modifier.width(16.dp))
            BookContent(
                book = book,
                isLocalFile = book.localFilePath != null,
                onClick = onActionClicked
            )
        }
    }
}
