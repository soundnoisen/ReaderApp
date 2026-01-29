package com.example.feature.upload.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.core.ui.component.BaseTextField
import com.example.feature.upload.R

@Composable
fun BookInfoForm(
    filePath: String,
    title: String,
    author: String,
    enabled: Boolean = true,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BaseTextField(
            value = filePath,
            enabled = false,
            onValueChange = {},
        )
        BaseTextField(
            value = title,
            enabled = enabled,
            onValueChange = onTitleChange,
            placeholder = stringResource(R.string.placeholder_book_title)
        )
        BaseTextField(
            value = author,
            enabled = enabled,
            onValueChange = onAuthorChange,
            placeholder = stringResource(R.string.placeholder_book_author)
        )
    }
}
