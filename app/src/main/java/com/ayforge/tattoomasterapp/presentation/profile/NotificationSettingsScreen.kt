package com.ayforge.tattoomasterapp.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    viewModel: NotificationSettingsViewModel = koinViewModel()
) {
    // Получаем начальные значения из ViewModel
    val reminderEnabledFromVm by viewModel.reminderEnabled.collectAsState()
    val reminderMinutesFromVm by viewModel.reminderMinutes.collectAsState()

    // --- УЛУЧШЕНИЕ: Локальные состояния для немедленного отклика UI ---
    var localReminderEnabled by remember { mutableStateOf(reminderEnabledFromVm) }
    var localReminderMinutes by remember { mutableStateOf(reminderMinutesFromVm) }

    // Синхронизируем локальное состояние с ViewModel, если оно изменилось извне
    LaunchedEffect(reminderEnabledFromVm, reminderMinutesFromVm) {
        localReminderEnabled = reminderEnabledFromVm
        localReminderMinutes = reminderMinutesFromVm
    }

    // --- УЛУЧШЕНИЕ: Сохраняем данные только когда локальное состояние изменилось ---
    LaunchedEffect(localReminderEnabled) {
        // Сохраняем, только если локальное значение отличается от того, что в ViewModel
        if (localReminderEnabled != reminderEnabledFromVm) {
            viewModel.setReminderEnabled(localReminderEnabled)
        }
    }

    LaunchedEffect(localReminderMinutes) {
        // Сохраняем, только если локальное значение отличается от того, что в ViewModel
        if (localReminderMinutes != reminderMinutesFromVm) {
            viewModel.setReminderMinutes(localReminderMinutes)
        }
    }
    // --- КОНЕЦ УЛУЧШЕНИЙ ---

    var expanded by remember { mutableStateOf(false) }
    val options = listOf(5, 10, 15, 30, 60, 120) // Добавил 15 для примера

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Уведомления") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Включить уведомления
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Напоминать о встречах",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = localReminderEnabled, // Используем локальное состояние
                    onCheckedChange = { isChecked ->
                        localReminderEnabled = isChecked // Обновляем локальное состояние
                    }
                )
            }

            // За сколько минут
            if (localReminderEnabled) { // Используем локальное состояние
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = "$localReminderMinutes минут", // Используем локальное состояние
                        onValueChange = {},
                        label = { Text("За сколько минут напоминать") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { minutes ->
                            DropdownMenuItem(
                                text = { Text("$minutes минут") },
                                onClick = {
                                    localReminderMinutes = minutes // Обновляем локальное состояние
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}