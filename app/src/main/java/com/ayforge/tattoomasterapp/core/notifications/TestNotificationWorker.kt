package com.ayforge.tattoomasterapp.core.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TestNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val id = System.currentTimeMillis().toInt()
        NotificationHelper.showNotification(
            context = applicationContext,
            channelId = NotificationHelper.CHANNEL_REMINDERS,
            title = "TattooMasterApp",
            message = "Это тестовое уведомление работает!",
            notificationId = id
        )
        return Result.success()
    }
}
