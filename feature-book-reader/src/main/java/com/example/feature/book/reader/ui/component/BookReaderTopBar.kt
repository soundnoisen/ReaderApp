package com.example.feature.book.reader.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.core.ui.component.BaseHeader
import com.example.feature.book.reader.R

@Composable
fun BookReaderTopBar(
    title: String,
    isSettingsVisible: Boolean,
    onBack: () -> Unit,
    onSettingsClick: (Boolean) -> Unit
) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(painter = painterResource(R.drawable.ic_bk), contentDescription = null, Modifier.size(20.dp))
            }
            BaseHeader(text = title)
            IconButton(
                modifier = Modifier.padding(end = 8.dp),
                onClick = { onSettingsClick(!isSettingsVisible) }
            ) {
                Icon(painter = painterResource(R.drawable.ic_reader_setting), contentDescription = null, Modifier.size(22.dp))
            }
        }
    }
}
