package com.ayforge.tattoomasterapp.presentation.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayforge.tattoomasterapp.presentation.appointment.AppointmentViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

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

    var now by remember { mutableStateOf(LocalTime.now()) }
    var menuExpanded by remember { mutableStateOf(false) }
    var compactView by remember { mutableStateOf(true) }

    // Загружаем встречи
    LaunchedEffect(date) {
        viewModel.loadAppointmentsForDay(date)
    }

    // Обновление текущего времени каждую минуту
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(60_000)
        }
    }

    val hourHeight = 60.dp
    val density = LocalDensity.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(dateLabel) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Компактный вид") },
                                onClick = {
                                    compactView = true
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Детальный вид") },
                                onClick = {
                                    compactView = false
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("appointment/new?date=$dateParam") }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Создать встречу")
            }
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val minutesPerHourPx = with(density) { hourHeight.toPx() }
                        val totalMinutes = (offset.y / minutesPerHourPx * 60).toInt()

                        val clickedHour = (totalMinutes / 60).coerceIn(0, 23)
                        val clickedMinute = (totalMinutes % 60).let { (it / 5) * 5 } // округляем до 5 минут

                        val clickedTime = LocalTime.of(clickedHour, clickedMinute)

                        navController.navigate(
                            "appointment/new?date=$dateParam&time=$clickedTime"
                        )
                    }
                }
        ) {
            // сетка часов
            Column {
                repeat(24) { hour ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(hourHeight),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "%02d:00".format(hour),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(50.dp)
                        )
                        Divider(
                            color = Color.LightGray,
                            thickness = 0.5.dp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // блоки встреч
            appointments.forEach { apptWithClient ->
                val appt = apptWithClient.appointment
                if (appt.startTime.toLocalDate() == date) {
                    val startMinutes = appt.startTime.toLocalTime().toSecondOfDay() / 60
                    val endMinutes = appt.endTime.toLocalTime().toSecondOfDay() / 60
                    val durationMinutes = endMinutes - startMinutes

                    val offsetY =
                        with(LocalDensity.current) { (startMinutes * hourHeight.toPx() / 60).toDp() }
                    val blockHeight =
                        with(LocalDensity.current) { (durationMinutes * hourHeight.toPx() / 60).toDp() }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = offsetY)
                            .height(blockHeight)
                            .padding(start = 60.dp, end = 8.dp)
                            .clickable {
                                navController.navigate("appointment/${appt.id}")
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            if (compactView) {
                                Text(
                                    text = apptWithClient.client.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${appt.startTime.toLocalTime()} - ${appt.endTime.toLocalTime()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            } else {
                                Text(
                                    text = apptWithClient.client.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (!appt.description.isNullOrBlank()) {
                                    Text(
                                        text = appt.description!!,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = "${appt.startTime.toLocalTime()} - ${appt.endTime.toLocalTime()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // красная линия "сейчас"
            if (date == LocalDate.now()) {
                val nowMinutes = now.toSecondOfDay() / 60
                val offsetY =
                    with(LocalDensity.current) { (nowMinutes * hourHeight.toPx() / 60).toDp() }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .offset { IntOffset(x = 0, y = offsetY.roundToPx()) }
                ) {
                    drawLine(
                        color = Color.Red,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}
