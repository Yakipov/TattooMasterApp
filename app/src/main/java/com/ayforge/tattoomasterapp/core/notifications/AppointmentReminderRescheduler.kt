package com.ayforge.tattoomasterapp.core.notifications

import android.content.Context
import com.ayforge.tattoomasterapp.core.settings.SettingsDataStore
import com.ayforge.tattoomasterapp.domain.repository.AppointmentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZoneId

class AppointmentReminderRescheduler(
    private val context: Context,
    private val appointmentRepository: AppointmentRepository,
    private val settingsDataStore: SettingsDataStore
) {

    fun rescheduleAll() {
        CoroutineScope(Dispatchers.IO).launch {

            // Проверяем настройки
            val enabled = settingsDataStore.reminderEnabled.first()
            if (!enabled) return@launch

            val minutesBefore = settingsDataStore.reminderMinutesBefore.first()

            // Берём будущие встречи
            val now = java.time.LocalDateTime.now()
            val appointments = appointmentRepository
                .getFutureAppointments(now)
                .first()

            // Планируем уведомления
            appointments.forEach { appointment ->

                val startMillis = appointment.startTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                // На всякий случай отменяем старое
                AppointmentNotificationScheduler.cancelReminder(
                    context = context,
                    appointmentId = appointment.id
                )

                // Создаём новое
                AppointmentNotificationScheduler.scheduleReminder(
                    context = context,
                    appointmentId = appointment.id,
                    appointmentTitle = appointment.description ?: "Встреча",
                    startTimeMillis = startMillis,
                    minutesBefore = minutesBefore
                )
            }
        }
    }
}
