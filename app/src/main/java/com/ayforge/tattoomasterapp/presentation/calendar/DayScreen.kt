package com.ayforge.tattoomasterapp.presentation.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayforge.tattoomasterapp.presentation.appointment.AppointmentViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    navController: NavController,
    date: LocalDate,
    viewModel: AppointmentViewModel = koinViewModel()
) {
    val dateLabel = remember(date) {
        date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    }
    val dateParam = remember(date) {
        date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    val appointments by viewModel.appointmentsForDay.collectAsState()

    LaunchedEffect(date) {
        viewModel.loadAppointmentsForDay(date)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(dateLabel) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("appointment/new?date=$dateParam")
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Создать встречу")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(24) { hour ->
                val hourAppointments = appointments.filter { it.appointment.startTime.hour == hour }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "%02d:00".format(hour),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(50.dp)
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        Divider(
                            color = Color.LightGray,
                            thickness = 0.5.dp,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            hourAppointments.forEach { apptWithClient ->
                                val appt = apptWithClient.appointment
                                val client = apptWithClient.client

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clickable {
                                            navController.navigate("appointment/${appt.id}")
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            "Клиент: ${client.name} (${client.phone})",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (!appt.description.isNullOrBlank()) {
                                            Text("Описание: ${appt.description}", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text(
                                            text = "Время: ${appt.startTime.toLocalTime()} - ${appt.endTime.toLocalTime()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
