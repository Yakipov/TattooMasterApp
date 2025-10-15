package com.ayforge.tattoomasterapp.presentation.appointment

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayforge.tattoomasterapp.presentation.appointment.components.CompleteAppointmentDialog
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    navController: NavController,
    appointmentId: Long,
    viewModel: AppointmentViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val appointmentWithClient by viewModel.selectedAppointment.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    // состояние для диалогов
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var reloadAfterComplete by remember { mutableStateOf(false) }

    // состояния для редактирования
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(11, 0)) }

    fun resetEditableStatesToSource() {
        appointmentWithClient?.let {
            val client = it.client
            val appt = it.appointment
            name = client.name
            phone = client.phone
            email = client.email ?: ""
            description = appt.description ?: ""
            startTime = appt.startTime.toLocalTime()
            endTime = appt.endTime.toLocalTime()
        }
    }

    LaunchedEffect(appointmentId) {
        viewModel.loadAppointmentById(appointmentId)
    }

    LaunchedEffect(appointmentWithClient, isEditing) {
        if (!isEditing) resetEditableStatesToSource()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEditing) "Редактирование" else "Детали встречи") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) {
                            resetEditableStatesToSource()
                            isEditing = false
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            if (isEditing) Icons.Filled.Close else Icons.Filled.ArrowBack,
                            contentDescription = if (isEditing) "Отмена" else "Назад"
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            val original = appointmentWithClient ?: return@IconButton
                            val updatedClient = original.client.copy(
                                name = name,
                                phone = phone,
                                email = email.takeIf { it.isNotBlank() }
                            )

                            val date = original.appointment.startTime.toLocalDate()
                            val updatedAppointment = original.appointment.copy(
                                startTime = LocalDateTime.of(date, startTime),
                                endTime = LocalDateTime.of(date, endTime),
                                description = description.takeIf { it.isNotBlank() }
                            )

                            viewModel.updateAppointment(updatedAppointment, updatedClient)
                            isEditing = false
                        }) {
                            Icon(Icons.Filled.Done, contentDescription = "Сохранить")
                        }
                    } else {
                        IconButton(onClick = {
                            resetEditableStatesToSource()
                            isEditing = true
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Редактировать")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (appointmentWithClient == null && !isEditing) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val appointment = appointmentWithClient?.appointment

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
                modifier = Modifier.fillMaxWidth(),
                readOnly = !isEditing
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = !isEditing
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = !isEditing
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = !isEditing
            )

            Button(
                onClick = {
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            val newStart = LocalTime.of(hour, minute)
                            val duration = java.time.Duration.between(startTime, endTime)
                            startTime = newStart
                            val newEnd = startTime.plus(duration)
                            endTime = if (newEnd <= startTime) startTime.plusHours(1) else newEnd
                        },
                        startTime.hour,
                        startTime.minute,
                        true
                    ).show()
                },
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Начало: %02d:%02d".format(startTime.hour, startTime.minute))
            }

            Button(
                onClick = {
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            val chosen = LocalTime.of(hour, minute)
                            endTime = if (chosen <= startTime) startTime.plusHours(1) else chosen
                        },
                        endTime.hour,
                        endTime.minute,
                        true
                    ).show()
                },
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Окончание: %02d:%02d".format(endTime.hour, endTime.minute))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка завершения встречи
            if (appointment != null && !isEditing && !appointment.isCompleted) {
                Button(
                    onClick = { showCompleteDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Завершить встречу")
                }
            }

            // Если встреча уже завершена — показать статус
            if (appointment?.isCompleted == true) {
                Text(
                    text = "Встреча завершена",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // Диалог завершения встречи
        if (showCompleteDialog && appointment != null) {
            CompleteAppointmentDialog(
                onDismiss = { showCompleteDialog = false },
                onConfirm = { amount, method, note ->
                    viewModel.completeAppointment(appointment.id, amount, method, note)
                    showCompleteDialog = false
                    reloadAfterComplete = true // 🔹 отметим, что нужно обновить данные
                }
            )
        }

        // После завершения встречи перезагружаем обновленные данные
        LaunchedEffect(reloadAfterComplete) {
            if (reloadAfterComplete && appointment != null) {
                viewModel.loadAppointmentById(appointment.id)
                reloadAfterComplete = false
            }
        }


        // Диалог подтверждения удаления
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Удалить встречу") },
                text = { Text("Вы уверены, что хотите удалить эту встречу?") },
                confirmButton = {
                    TextButton(onClick = {
                        appointmentWithClient?.appointment?.let {
                            viewModel.deleteAppointment(it)
                            navController.popBackStack()
                        }
                        showDeleteDialog = false
                    }) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Нет")
                    }
                }
            )
        }
    }
}
