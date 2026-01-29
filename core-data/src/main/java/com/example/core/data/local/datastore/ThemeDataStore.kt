package com.example.core.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.ThemeDataStore by preferencesDataStore(name = "theme")
