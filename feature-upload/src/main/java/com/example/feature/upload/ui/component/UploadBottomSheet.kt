package com.example.feature.upload.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.book.UploadProgress
import com.example.core.ui.component.BaseButton
import com.example.feature.upload.R
import com.example.feature.upload.ui.UploadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadBottomSheet(
    state: UploadState,
    sheetState: SheetState,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onUploadClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BookInfoForm(
                filePath = state.uri?.toString() ?: "",
                title = state.title,
                author = state.author,
                onTitleChange = onTitleChange,
                onAuthorChange = onAuthorChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            BaseButton(
                text = stringResource(R.string.action_download),
                enabled = state.progress !is UploadProgress.Uploading
            ) {
                onUploadClicked()
            }
        }
    }
}
