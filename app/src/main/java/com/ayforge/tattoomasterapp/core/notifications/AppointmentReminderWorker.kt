package com.ayforge.tattoomasterapp.core.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class AppointmentReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Напоминание о встрече"
        val message = inputData.getString("message") ?: "У вас скоро встреча!"
        val notificationId = inputData.getInt("notificationId", System.currentTimeMillis().toInt())

        NotificationHelper.showNotification(
            context = applicationContext,
            channelId = NotificationHelper.CHANNEL_REMINDERS,
            title = title,
            message = message,
            notificationId = notificationId
        )

        return Result.success()
    }
}
