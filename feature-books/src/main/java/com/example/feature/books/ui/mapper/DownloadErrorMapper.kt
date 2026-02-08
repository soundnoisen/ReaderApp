package com.example.feature.books.ui.mapper

import android.content.res.Resources
import com.example.core.domain.model.book.DownloadError
import com.example.feature.books.R

fun DownloadError.toUiText(resources: Resources): String {
    return when (this) {
        is DownloadError.CoverGenerationFailed -> resources.getString(R.string.error_cover_generation_failed)
        is DownloadError.FileNotFound -> resources.getString(R.string.error_file_not_found)
        is DownloadError.Network -> resources.getString(R.string.error_network)
        is DownloadError.Unknown -> resources.getString(R.string.error_unknown_error)
    }
}
