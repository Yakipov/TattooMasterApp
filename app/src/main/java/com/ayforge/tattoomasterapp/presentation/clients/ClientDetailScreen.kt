package com.ayforge.tattoomasterapp.presentation.clients

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    clientId: Long,
    navController: NavController,
    viewModel: ClientDetailViewModel = getViewModel()
) {
    val clientWithAppointments by viewModel.clientWithAppointments.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(clientId) {
        viewModel.loadClient(clientId)
    }

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Клиент") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // TODO: Навигация на экран редактирования клиента
                        navController.navigate("edit_client/$clientId")
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            clientWithAppointments?.let { (client, appointments) ->
                Text(text = client.name, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text(text = "Телефон: ${client.phone}")
                Text(text = "Email: ${client.email ?: ""}")

                Spacer(Modifier.height(16.dp))
                Text(text = "История встреч", style = MaterialTheme.typography.titleMedium)

                appointments.forEach { appointment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Начало: ${appointment.startTime.format(dateFormatter)}")
                            Text("Окончание: ${appointment.endTime.format(dateFormatter)}")
                            appointment.description?.let { description ->
                                Text("Описание: $description")
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteClient(clientId)
                    showDeleteDialog = false
                    navController.popBackStack() // назад после удаления
                }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            },
            title = { Text("Удалить клиента?") },
            text = { Text("Все встречи этого клиента будут удалены!") }
        )
    }
}
