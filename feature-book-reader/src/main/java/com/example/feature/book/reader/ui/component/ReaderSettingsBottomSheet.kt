package com.example.feature.book.reader.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.ui.component.ThemeSetting
import com.example.feature.book.reader.ui.BookReaderState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsBottomSheet(
    settings: BookReaderState,
    isDarkTheme: Boolean,
    onFontDec:() -> Unit,
    onFontInc:() -> Unit,
    onLineDec:() -> Unit,
    onLineInc:() -> Unit,
    onThemeChanged: (Boolean) -> Unit,
    onDismiss:() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            FontSizeSetting(
                value = settings.fontSizeSp,
                onDec = onFontDec,
                onInc = onFontInc
            )
            HorizontalDivider()
            LineHeightSetting(
                value = settings.lineHeightPercent,
                onDec = onLineDec,
                onInc = onLineInc
            )
            HorizontalDivider()
            ThemeSetting(
                isDarkTheme = isDarkTheme,
                onChange = onThemeChanged,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}