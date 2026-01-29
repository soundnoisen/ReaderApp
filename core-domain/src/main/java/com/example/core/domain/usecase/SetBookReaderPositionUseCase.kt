package com.example.core.domain.usecase

import com.example.core.domain.model.reader.ReaderPosition
import com.example.core.domain.repository.ReaderPositionRepository
import jakarta.inject.Inject

class SetBookReaderPositionUseCase @Inject constructor(
    private val repository: ReaderPositionRepository
) {
    suspend operator fun invoke(bookId: String, position: ReaderPosition) {
        repository.setPosition(bookId, position)
    }
}