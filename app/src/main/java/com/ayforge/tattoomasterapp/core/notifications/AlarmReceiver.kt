package com.ayforge.tattoomasterapp.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    // --- ИЗМЕНЕНИЕ ЗДЕСЬ ---
    // Переносим константу в companion object, чтобы она была доступна на уровне класса,
    // а не для каждого отдельного экземпляра.
    companion object {
        private const val TAG = "AlarmReceiver_DEBUG" // Тег для фильтрации
    }
    // --- КОНЕЦ ИЗМЕНЕНИЙ ---

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "СРАБОТАЛ! Получен интент с action: ${intent.action}")

        val id = intent.getLongExtra("EXTRA_ID", -1L)
        if (id == -1L) {
            Log.e(TAG, "ОШИБКА: ID встречи не был передан в AlarmReceiver! Показ уведомления невозможен.")
            return
        }

        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Напоминание"
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Встреча скоро начнется"

        Log.d(TAG, "Показ уведомления для ID: $id. Канал: ${NotificationHelper.CHANNEL_ID}, Заголовок: '$title', Сообщение: '$message'")

        NotificationHelper.showNotification(
            context = context,
            channelId = NotificationHelper.CHANNEL_ID,
            title = title,
            message = message,
            notificationId = id.toInt()
        )
    }
}
