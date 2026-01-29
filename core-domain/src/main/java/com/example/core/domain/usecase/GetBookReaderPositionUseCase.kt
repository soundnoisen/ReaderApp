package com.example.core.domain.usecase

import com.example.core.domain.model.reader.ReaderPosition
import com.example.core.domain.repository.ReaderPositionRepository
import jakarta.inject.Inject

class GetBookReaderPositionUseCase @Inject constructor(
    private val repository: ReaderPositionRepository
) {
    suspend operator fun invoke(bookId: String): ReaderPosition? {
        return repository.getPosition(bookId)
    }
}