package com.example.feature.upload.data.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun queryFileName(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        it.moveToFirst()
        it.getString(nameIndex)
    } ?: "file_${System.currentTimeMillis()}"
}