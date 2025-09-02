package com.ayforge.tattoomasterapp.core.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore by preferencesDataStore(name = "app_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
    }

    val selectedLanguage: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY]
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }

    suspend fun getLanguage(): String {
        return selectedLanguage
            .map { it ?: "en" } // default fallback
            .first()
    }
}
