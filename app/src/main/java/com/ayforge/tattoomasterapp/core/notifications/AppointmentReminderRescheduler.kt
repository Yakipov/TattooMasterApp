package com.ayforge.tattoomasterapp.core.notifications

import android.content.Context
import com.ayforge.tattoomasterapp.core.notifications.AlarmScheduler
import com.ayforge.tattoomasterapp.core.settings.SettingsDataStore
import com.ayforge.tattoomasterapp.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId

class AppointmentReminderRescheduler(
    private val context: Context,
    private val appointmentRepository: AppointmentRepository,
    private val settingsDataStore: SettingsDataStore
) {

    suspend fun rescheduleAll() {

        // 1️⃣ Проверяем настройки
        val enabled = settingsDataStore.reminderEnabled.first()
        if (!enabled) {
            // Если уведомления выключены, можно отменить все существующие "будильники"
            // (это опциональный, но хороший шаг)
            val appointments = appointmentRepository.getFutureAppointments(LocalDateTime.now()).first()
            appointments.forEach { AlarmScheduler.cancel(context, it.id) }
            return
        }

        val minutesBefore = settingsDataStore.reminderMinutesBefore.first()

        // 2️⃣ Берём будущие встречи из Room
        val now = LocalDateTime.now()

        val appointments = appointmentRepository
            .getFutureAppointments(now)
            .first()

        // 3️⃣ Ставим уведомления заново с помощью AlarmScheduler
        appointments.forEach { appointment ->

            // Сначала отменяем старое (на случай, если оно было)
            AlarmScheduler.cancel(
                context = context,
                appointmentId = appointment.id
            )

            // Вычисляем время срабатывания
            val triggerAtMillis = appointment.startTime.minusMinutes(minutesBefore.toLong())
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            // Проверяем, что время еще не прошло
            if (triggerAtMillis > System.currentTimeMillis()) {
                // И планируем новое с помощью AlarmScheduler
                AlarmScheduler.schedule(
                    context = context,
                    appointmentId = appointment.id,
                    triggerAtMillis = triggerAtMillis,
                    title = appointment.description ?: "Встреча"
                )
            }
        }
    }
}
