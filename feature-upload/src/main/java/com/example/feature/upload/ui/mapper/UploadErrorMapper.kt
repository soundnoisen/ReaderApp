package com.example.feature.upload.ui.mapper

import android.content.res.Resources
import com.example.core.domain.model.book.UploadError
import com.example.feature.upload.R

fun UploadError.toUiText(resources: Resources): String {
    return when (this) {
        is UploadError.Network -> resources.getString(R.string.error_network)
        is UploadError.InvalidFile -> resources.getString(R.string.error_invalid_file)
        is UploadError.InvalidData -> resources.getString(R.string.error_invalid_data)
        is UploadError.Unknown -> resources.getString(R.string.error_unknown_error)
    }
}