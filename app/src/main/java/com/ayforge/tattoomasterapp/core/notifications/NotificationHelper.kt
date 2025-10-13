package com.ayforge.tattoomasterapp.core.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ayforge.tattoomasterapp.MainActivity
import com.ayforge.tattoomasterapp.R

object NotificationHelper {

    const val CHANNEL_REMINDERS = "reminders_channel"
    const val CHANNEL_GENERAL = "general_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Напоминания",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о встречах и напоминания"
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "Общие уведомления",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Все остальные уведомления"
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(reminderChannel)
            manager.createNotificationChannel(generalChannel)
        }
    }

    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        notificationId: Int
    ) {
        // ✅ PendingIntent — откроет MainActivity с фокусом на календарь
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "calendar") // ключ можно обработать в MainActivity/NavGraph
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)

        // ✅ Проверяем разрешение на уведомления (Android 13+)
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationId, builder.build())
        } else {
            android.util.Log.w("NotificationHelper", "Нет разрешения POST_NOTIFICATIONS")
        }
    }
}
