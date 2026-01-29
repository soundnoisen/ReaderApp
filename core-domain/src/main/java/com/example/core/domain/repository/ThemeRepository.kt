package com.example.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getIsDarkTheme(): Flow<Boolean>
    suspend fun setDarkTheme(isDark: Boolean)
}