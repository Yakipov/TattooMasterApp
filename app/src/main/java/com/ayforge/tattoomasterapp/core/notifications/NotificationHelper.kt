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

    const val CHANNEL_ID = "reminders_channel" // У вас уже есть эта или похожая константа

    // 1. Добавляем ID для общих уведомлений (из Firebase)
    const val CHANNEL_GENERAL = "general_channel"

    // 2. Добавляем метод для создания каналов
    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                CHANNEL_ID,
                "Напоминания о встречах",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о предстоящих записях"
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "Общие уведомления",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Новости и общая информация"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(reminderChannel)
            manager.createNotificationChannel(generalChannel)
        }
    }

    // 3. Добавляем универсальный метод для показа уведомлений
    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        notificationId: Int
    ) {
        // Здесь можно добавить логику открытия нужного экрана по нажатию
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId, // Используем уникальный ID для каждого PendingIntent
            context.packageManager.getLaunchIntentForPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Иконка, которую вы добавили
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

}
