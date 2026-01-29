package com.example.feature.upload.data.util

import android.content.Context
import android.net.Uri
import java.io.File

fun copyToCache(context: Context, uri: Uri): File {
    val fileName = queryFileName(context, uri)

    val booksDir = File(context.filesDir, "books").apply { if (!exists()) mkdirs() }
    val file = File(booksDir, fileName)

    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return file
}
