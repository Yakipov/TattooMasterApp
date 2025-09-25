package com.ayforge.tattoomasterapp.presentation.appointment

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAppointmentScreen(
    navController: NavController,
    date: LocalDate,
    time: LocalTime? = null, // 🔑 новое
    viewModel: AppointmentViewModel = koinViewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // округляем текущее время до 15 минут
    fun roundToQuarterHour(time: LocalTime): LocalTime {
        val minute = (time.minute / 15) * 15
        return time.withMinute(minute).withSecond(0).withNano(0)
    }

    var startTime by remember {
        mutableStateOf(
            time ?: roundToQuarterHour(LocalTime.now())
        )
    }
    var endTime by remember { mutableStateOf(startTime.plusHours(1)) }
    var manuallyChangedEnd by remember { mutableStateOf(false) }

    val clientCheckResult by viewModel.clientCheckResult.collectAsState()
    var showConflictDialog by remember { mutableStateOf(false) }

    // Загружаем список встреч для выбранного дня
    LaunchedEffect(date) {
        viewModel.loadAppointmentsForDay(date)
    }

    // Проверка клиента по имени и телефону
    LaunchedEffect(name, phone) {
        if (name.isNotBlank() && phone.isNotBlank()) {
            viewModel.checkClient(name, phone)
        } else {
            viewModel.clearClientCheck()
        }
    }

    if (showConflictDialog) {
        AlertDialog(
            onDismissRequest = { showConflictDialog = false },
            confirmButton = {
                TextButton(onClick = { showConflictDialog = false }) {
                    Text("Ок")
                }
            },
            title = { Text("Конфликт времени") },
            text = { Text("На это время уже назначена другая встреча.") }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Новая встреча") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя клиента") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth()
            )

            if (clientCheckResult is ClientCheckResult.ExistingClient) {
                Text(
                    text = "⚠ Клиент с такими данными уже существует. Он будет использован.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (необязательно)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth()
            )

            // Выбор времени начала
            Button(onClick = {
                TimePickerDialog(
                    context,
                    { _, hour: Int, minute: Int ->
                        val newStart = LocalTime.of(hour, minute)
                        startTime = newStart
                        if (!manuallyChangedEnd) {
                            endTime = startTime.plusHours(1)
                        } else if (endTime <= startTime) {
                            endTime = startTime.plusHours(1)
                        }
                    },
                    startTime.hour,
                    startTime.minute,
                    true
                ).show()
            }) {
                Text("Начало: %02d:%02d".format(startTime.hour, startTime.minute))
            }

            // Выбор времени окончания
            Button(onClick = {
                TimePickerDialog(
                    context,
                    { _, hour: Int, minute: Int ->
                        val chosenEnd = LocalTime.of(hour, minute)
                        endTime = if (chosenEnd <= startTime) {
                            startTime.plusHours(1)
                        } else {
                            chosenEnd
                        }
                        manuallyChangedEnd = true
                    },
                    endTime.hour,
                    endTime.minute,
                    true
                ).show()
            }) {
                Text("Окончание: %02d:%02d".format(endTime.hour, endTime.minute))
            }

            // Быстрые кнопки длительности
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        endTime = startTime.plusMinutes(30)
                        manuallyChangedEnd = true
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("+30 м") }

                Button(
                    onClick = {
                        endTime = startTime.plusHours(1)
                        manuallyChangedEnd = true
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("+1 ч") }

                Button(
                    onClick = {
                        endTime = startTime.plusHours(2)
                        manuallyChangedEnd = true
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("+2 ч") }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val startDateTime = LocalDateTime.of(date, startTime)
                    var endDateTime = LocalDateTime.of(date, endTime)

                    if (endDateTime <= startDateTime) {
                        endDateTime = startDateTime.plusHours(1)
                    }

                    // 🔑 проверка пересечения
                    if (viewModel.hasOverlap(startDateTime, endDateTime)) {
                        showConflictDialog = true
                        return@Button
                    }

                    if (clientCheckResult is ClientCheckResult.ExistingClient) {
                        val existing = (clientCheckResult as ClientCheckResult.ExistingClient).client
                        viewModel.createAppointment(
                            client = existing,
                            startTime = startDateTime,
                            endTime = endDateTime,
                            description = description
                        )
                    } else {
                        val newClient = ClientEntity(
                            id = 0L,
                            name = name,
                            phone = phone,
                            email = email,
                            userId = "" // заглушка, в репозитории заменится на currentUserId()
                        )
                        viewModel.createAppointment(
                            client = newClient,
                            startTime = startDateTime,
                            endTime = endDateTime,
                            description = description
                        )
                    }

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}
