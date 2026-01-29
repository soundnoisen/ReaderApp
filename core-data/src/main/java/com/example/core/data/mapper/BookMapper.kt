package com.example.core.data.mapper

import com.example.core.data.local.room.BookEntity
import com.example.core.domain.model.Book

fun BookEntity.toDomain() = Book(
    id = id,
    title = title,
    author = author,
    fileUrl = fileUrl,
    localFilePath = localFilePath,
    coverPath = coverPath,
    readingProgress = readingProgress,
)