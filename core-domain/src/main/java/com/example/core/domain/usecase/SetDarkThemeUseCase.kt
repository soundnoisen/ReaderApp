package com.example.core.domain.usecase

import com.example.core.domain.repository.ThemeRepository
import javax.inject.Inject

class SetDarkThemeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    suspend fun invoke(isDark: Boolean) = repository.setDarkTheme(isDark)
}