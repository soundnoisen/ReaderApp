package com.example.core.domain.model

import com.example.core.domain.model.book.DownloadProgress

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val fileUrl: String,
    val localFilePath: String?,
    val coverPath: String?,
    val readingProgress: Float = 0f,
    val downloadProgress: DownloadProgress = DownloadProgress.Idle
)