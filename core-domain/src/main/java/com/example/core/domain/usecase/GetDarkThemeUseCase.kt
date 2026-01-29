package com.example.core.domain.usecase

import com.example.core.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDarkThemeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    fun invoke(): Flow<Boolean> = repository.getIsDarkTheme()
}