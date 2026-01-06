package com.ayforge.tattoomasterapp.core.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import androidx.work.WorkManager

object AppointmentNotificationScheduler {

    fun scheduleReminder(
        context: Context,
        appointmentId: Long,
        appointmentTitle: String,
        startTimeMillis: Long,
        minutesBefore: Int
    ) {
        val triggerAt = startTimeMillis - minutesBefore * 60_000
        val delay = triggerAt - System.currentTimeMillis()
        if (delay <= 0) return // Уже поздно уведомлять

        val data = Data.Builder()
            .putLong("notificationId", appointmentId)
            .putString("title", "Напоминание о встрече")
            .putString("message", "Встреча \"$appointmentTitle\" начнется через $minutesBefore мин.")
            .build()

        val request = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("appointment_reminder")
            .addTag("appointment_reminder_$appointmentId")
            .build()


        WorkManager.getInstance(context).enqueueUniqueWork(
            "appointment_reminder_$appointmentId",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelReminder(context: Context, appointmentId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("appointment_reminder_$appointmentId")
    }

    fun cancelAllReminders(context: Context) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("appointment_reminder")
    }
}
