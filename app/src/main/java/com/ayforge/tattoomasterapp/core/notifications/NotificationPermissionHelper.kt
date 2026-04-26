package com.ayforge.tattoomasterapp.core.notifications

import android.content.Context
import android.os.PowerManager
import androidx.core.app.NotificationManagerCompat

object NotificationPermissionHelper {

    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }
}