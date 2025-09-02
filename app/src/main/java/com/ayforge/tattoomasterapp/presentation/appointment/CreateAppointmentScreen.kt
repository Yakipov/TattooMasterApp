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
    viewModel: AppointmentViewModel = koinViewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var startTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
    var endTime by remember { mutableStateOf(startTime.plusHours(1)) }

    // 🔑 флаг: пользователь сам правил время конца
    var manuallyChangedEnd by remember { mutableStateOf(false) }

    val clientCheckResult by viewModel.clientCheckResult.collectAsState()

    // Проверка клиента по имени и телефону
    LaunchedEffect(name, phone) {
        if (name.isNotBlank() && phone.isNotBlank()) {
            viewModel.checkClient(name, phone)
        } else {
            viewModel.clearClientCheck()
        }
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

                        // если конец не трогали руками → всегда +1ч
                        if (!manuallyChangedEnd) {
                            endTime = startTime.plusHours(1)
                        } else if (endTime <= startTime) {
                            // защита от пересечения
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
                        manuallyChangedEnd = true // пользователь сам задал
                    },
                    endTime.hour,
                    endTime.minute,
                    true
                ).show()
            }) {
                Text("Окончание: %02d:%02d".format(endTime.hour, endTime.minute))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val startDateTime = LocalDateTime.of(date, startTime)
                    var endDateTime = LocalDateTime.of(date, endTime)

                    if (endDateTime <= startDateTime) {
                        endDateTime = startDateTime.plusHours(1)
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
                            email = email
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
