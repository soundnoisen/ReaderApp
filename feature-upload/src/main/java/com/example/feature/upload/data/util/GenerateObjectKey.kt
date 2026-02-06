package com.example.feature.upload.data.util

import java.io.File
import java.util.UUID

internal fun generateObjectKey(file: File): String {
    val ext = file.extension
    return "books/${UUID.randomUUID()}.$ext"
}