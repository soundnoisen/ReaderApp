package com.example.feature.upload.domain.validator

import android.net.Uri
import com.example.core.domain.model.book.UploadError
import jakarta.inject.Inject

class UploadValidator @Inject constructor() {

    fun validateField(field: String): UploadError? {
        if (field.isBlank()) return UploadError.InvalidData
        return null
    }

    fun validExtension(name: String): UploadError? {
        return if (name.substringAfterLast('.', "")
                .lowercase() in setOf("txt", "epub", "pdf")) {
            null
        } else UploadError.InvalidFile
    }
}
