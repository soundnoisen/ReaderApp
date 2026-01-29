package com.example.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.core.data.local.datastore.ThemeDataStore
import com.example.core.domain.repository.ThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
): ThemeRepository {

    private val dataStore = context.ThemeDataStore

    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    override fun getIsDarkTheme(): Flow<Boolean> =
        dataStore.data.map { it[DARK_THEME_KEY] ?: false }

    override suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit {
            it[DARK_THEME_KEY] = isDark
        }
    }
}