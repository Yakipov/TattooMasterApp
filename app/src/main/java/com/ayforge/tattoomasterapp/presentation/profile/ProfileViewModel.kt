package com.ayforge.tattoomasterapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.core.notifications.AppointmentNotificationScheduler
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.core.settings.SettingsDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.Context

class ProfileViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val sessionManager: SessionManager,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _userState = MutableStateFlow<FirebaseUser?>(null)
    val userState: StateFlow<FirebaseUser?> = _userState.asStateFlow()

    init {
        _userState.value = firebaseAuth.currentUser
    }

    fun logout(context: Context) {
        AppointmentNotificationScheduler.cancelAllReminders(context)

        firebaseAuth.signOut()
        sessionManager.clearSession()
    }


    // ---------- Уведомления ----------
    val reminderEnabled = settingsDataStore.reminderEnabled.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), true
    )

    val reminderMinutesBefore = settingsDataStore.reminderMinutesBefore.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), 60
    )

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setReminderEnabled(enabled) }
    }

    fun setReminderMinutes(minutes: Int) {
        viewModelScope.launch { settingsDataStore.setReminderMinutesBefore(minutes) }
    }
}
