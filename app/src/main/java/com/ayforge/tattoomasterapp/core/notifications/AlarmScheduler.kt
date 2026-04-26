package com.ayforge.tattoomasterapp.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log // <--- УБЕДИТЕСЬ, ЧТО ЭТОТ ИМПОРТ ЕСТЬ

object AlarmScheduler {

    private const val TAG = "AlarmScheduler_DEBUG" // Тег для фильтрации в Logcat

    fun schedule(
        context: Context,
        appointmentId: Long,
        triggerAtMillis: Long,
        title: String
    ) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "APPOINTMENT_REMINDER_$appointmentId"
            putExtra("EXTRA_ID", appointmentId)
            putExtra("EXTRA_TITLE", "Напоминание о встрече")
            putExtra("EXTRA_MESSAGE", title)
        }

        Log.d(TAG, "Планирование уведомления для ID: $appointmentId на время: $triggerAtMillis")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appointmentId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e(TAG, "ОШИБКА: Нет разрешения SCHEDULE_EXACT_ALARM. Уведомление не будет запланировано.")
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
        Log.i(TAG, "УСПЕХ: Уведомление для ID: $appointmentId успешно запланировано.")
    }

    fun cancel(context: Context, appointmentId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "APPOINTMENT_REMINDER_$appointmentId"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appointmentId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Отмена уведомления для ID: $appointmentId")
    }
}
