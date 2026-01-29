package com.example.feature.upload.ui

import android.net.Uri
import com.example.core.domain.model.book.UploadProgress

data class UploadState(
    val title: String = "",
    val author: String = "",
    val fileName: String = "",
    val uri: Uri? = null,
    val progress: UploadProgress = UploadProgress.Idle,
    val isBottomSheetVisible: Boolean = false,
)
