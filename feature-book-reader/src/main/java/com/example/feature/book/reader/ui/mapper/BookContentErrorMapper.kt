package com.example.feature.book.reader.ui.mapper

import android.content.res.Resources
import com.example.core.domain.model.reader.BookContentError
import com.example.feature.book.reader.R

fun BookContentError.toUiText(resources: Resources): String {
    return when (this) {
        BookContentError.FileNotFound -> resources.getString(R.string.error_file_not_found)
        BookContentError.InvalidFormat -> resources.getString(R.string.error_invalid_format)
        BookContentError.UnableReadFile -> resources.getString(R.string.error_unable_read_file)
        BookContentError.EmptyFile -> resources.getString(R.string.error_empty_file)
    }
}
