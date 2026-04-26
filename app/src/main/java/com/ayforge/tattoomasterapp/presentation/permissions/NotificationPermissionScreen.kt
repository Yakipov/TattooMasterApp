package com.ayforge.tattoomasterapp.presentation.permissions

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionScreen(onNext: () -> Unit) {
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 1. Состояние разрешения на показ уведомлений (Android 13+)
    val postNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    // 2. Состояние разрешения на точные будильники (Android 12+)
    var hasExactAlarmPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms()
            else true
        )
    }

    // Каждую секунду проверяем, не дал ли пользователь разрешение в настройках
    LaunchedEffect(Unit) {
        while (true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                hasExactAlarmPermission = alarmManager.canScheduleExactAlarms()
            }
            // Если все разрешения даны - идем дальше автоматически
            if ((postNotificationPermission?.status?.isGranted ?: true) && hasExactAlarmPermission) {
                onNext()
                break
            }
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Настройка уведомлений", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Для работы напоминаний о встречах приложению нужны разрешения.")

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка 1: Основные уведомления
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            Button(onClick = { postNotificationPermission.launchPermissionRequest() }) {
                Text("Разрешить уведомления")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка 2: Точные будильники (отправляет в настройки системы)
        if (!hasExactAlarmPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Button(onClick = {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            }) {
                Text("Разрешить точные напоминания")
            }
            Text(
                "Найдите TattooMasterApp в списке и включите тумблер",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}