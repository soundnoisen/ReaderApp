package com.example.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.core.data.local.datastore.ReaderPositionDataStore
import com.example.core.domain.model.reader.ReaderPosition
import com.example.core.domain.repository.ReaderPositionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class ReaderPositionRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
): ReaderPositionRepository {

    private val dataStore = context.ReaderPositionDataStore

    object ReaderPrefsKeys {
        fun anchorIndex(bookId: String) = intPreferencesKey("anchor_index_$bookId")
        fun progressInItem(bookId: String) = floatPreferencesKey("progress_in_item_$bookId")
    }

    override suspend fun getPosition(bookId: String): ReaderPosition? {
        val prefs = dataStore.data.first()
        val index = prefs[ReaderPrefsKeys.anchorIndex(bookId)] ?: return null
        val progress = prefs[ReaderPrefsKeys.progressInItem(bookId)] ?: 0f
        return ReaderPosition(index, progress)
    }

    override suspend fun setPosition(bookId: String, position: ReaderPosition) {
        dataStore.edit { prefs ->
            prefs[ReaderPrefsKeys.anchorIndex(bookId)] = position.anchorIndex
            prefs[ReaderPrefsKeys.progressInItem(bookId)] = position.progressInItem
        }
    }

    override suspend fun deletePosition(bookId: String) {
        dataStore.edit { prefs ->
            prefs.remove(ReaderPrefsKeys.anchorIndex(bookId))
            prefs.remove(ReaderPrefsKeys.progressInItem(bookId))
        }
    }
}