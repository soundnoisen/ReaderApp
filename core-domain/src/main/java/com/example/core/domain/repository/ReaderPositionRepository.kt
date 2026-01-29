package com.example.core.domain.repository

import com.example.core.domain.model.reader.ReaderPosition

interface ReaderPositionRepository {
    suspend fun getPosition(bookId: String): ReaderPosition?
    suspend fun setPosition(bookId: String, position: ReaderPosition)
    suspend fun deletePosition(bookId: String)
}
