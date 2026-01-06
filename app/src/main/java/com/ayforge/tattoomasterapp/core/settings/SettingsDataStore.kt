package com.ayforge.tattoomasterapp.core.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        private val REMINDER_MINUTES = intPreferencesKey("reminder_minutes_before")
    }

    val reminderEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[REMINDER_ENABLED] ?: true } // по умолчанию включено

    val reminderMinutesBefore: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[REMINDER_MINUTES] ?: 60 } // по умолчанию 60 мин

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[REMINDER_ENABLED] = enabled }
    }

    suspend fun setReminderMinutesBefore(minutes: Int) {
        context.dataStore.edit { it[REMINDER_MINUTES] = minutes }
    }
}
