package com.ayforge.tattoomasterapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.core.settings.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationSettingsViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val reminderEnabled = settingsDataStore.reminderEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    val reminderMinutes = settingsDataStore.reminderMinutesBefore
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 60
        )

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setReminderEnabled(enabled)
        }
    }

    fun setReminderMinutes(minutes: Int) {
        viewModelScope.launch {
            settingsDataStore.setReminderMinutesBefore(minutes)
        }
    }
}
