package com.example.feature.book.reader.domain.usecase

import com.example.core.domain.model.reader.BookContent
import com.example.feature.book.reader.domain.repository.BookReaderRepository
import jakarta.inject.Inject

class GetBookContentUseCase @Inject constructor(
    private val repository: BookReaderRepository
) {
    suspend operator fun invoke(localPath: String): BookContent {
        return repository.getBookContent(localPath)
    }
}