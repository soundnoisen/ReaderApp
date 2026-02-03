package com.example.feature.upload.ui

import com.example.core.domain.model.book.UploadError

sealed class UploadEffect {
    object OpenFilePicker: UploadEffect()
    data class ShowError(val error: UploadError): UploadEffect()
    object UploadSuccess: UploadEffect()
}
