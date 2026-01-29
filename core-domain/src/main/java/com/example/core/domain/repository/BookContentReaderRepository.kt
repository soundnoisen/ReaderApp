package com.example.core.domain.repository

import com.example.core.domain.model.reader.BookContent

interface BookContentReaderRepository {
    suspend fun getBookContent(localPath: String): BookContent
}