package com.example.feature.book.reader.domain.usecase

import com.example.feature.book.reader.domain.repository.BookReaderRepository
import javax.inject.Inject

class UpdateBookReadingProgressUseCase @Inject constructor(
    private val repository: BookReaderRepository
) {
    suspend operator fun invoke(bookId: String, progress: Float) {
        repository.updateBookReadingProgress(bookId, progress)
    }
}