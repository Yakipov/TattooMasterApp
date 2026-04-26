package com.ayforge.tattoomasterapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.core.notifications.AppointmentReminderRescheduler
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.core.settings.SettingsDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val sessionManager: SessionManager,
    private val settingsDataStore: SettingsDataStore,
    private val reminderRescheduler: AppointmentReminderRescheduler
) : ViewModel() {

    // ---------- Пользователь ----------
    private val _userState = MutableStateFlow<FirebaseUser?>(null)
    val userState: StateFlow<FirebaseUser?> = _userState.asStateFlow()

    init {
        _userState.value = firebaseAuth.currentUser
    }

    fun logout() {
        firebaseAuth.signOut()
        sessionManager.clearSession()
    }

    // ---------- Уведомления ----------
    val reminderEnabled = settingsDataStore.reminderEnabled.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        true
    )

    val reminderMinutesBefore = settingsDataStore.reminderMinutesBefore.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        60
    )

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setReminderEnabled(enabled)

            // 🔁 перепланируем ВСЕ уведомления
            reminderRescheduler.rescheduleAll()
        }
    }

    fun setReminderMinutes(minutes: Int) {
        viewModelScope.launch {
            settingsDataStore.setReminderMinutesBefore(minutes)

            // 🔁 перепланируем ВСЕ уведомления
            reminderRescheduler.rescheduleAll()
        }
    }
}
