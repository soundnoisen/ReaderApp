package com.example.feature.upload.ui

import android.net.Uri

sealed class UploadIntent {
    object SelectFile: UploadIntent()
    data class FileSelected(val uri: Uri, val fileName: String): UploadIntent()
    data class TitleChanged(val title: String): UploadIntent()
    data class AuthorChanged(val author: String): UploadIntent()
    object ClearSelectedFile: UploadIntent()
    object UploadClicked: UploadIntent()
    object RetryClicked: UploadIntent()
}