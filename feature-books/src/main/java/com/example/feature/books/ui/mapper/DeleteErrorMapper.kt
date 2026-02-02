package com.example.feature.books.ui.mapper

import android.content.res.Resources
import com.example.core.domain.model.book.DeleteError
import com.example.feature.books.R

fun DeleteError.toUiText(resources: Resources): String {
    return when (this) {
        is DeleteError.Network -> resources.getString(R.string.error_network)
        is DeleteError.Unknown -> resources.getString(R.string.error_unknown_error) + error
    }
}