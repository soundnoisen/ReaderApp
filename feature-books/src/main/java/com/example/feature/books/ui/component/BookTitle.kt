package com.example.feature.books.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun BookTitle(text: String, isLocalFile: Boolean) {
    Text(
        text = text,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        maxLines = 1,
        color = MaterialTheme.colorScheme.onBackground.copy(if (isLocalFile) 1f else 0.5f),
        overflow = TextOverflow.Ellipsis,
    )
}
