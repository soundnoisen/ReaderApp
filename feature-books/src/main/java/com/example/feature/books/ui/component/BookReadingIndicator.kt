package com.example.feature.books.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun BookReadingIndicator(
    isLocalFile: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(
            modifier = Modifier
                .weight(6f)
                .height(6.dp)
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(if (isLocalFile) 1f else 0.5f), shape = RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(if (isLocalFile) progress.coerceIn(0f, 1f) else 0f)
                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp))
            )
        }
    }
}


