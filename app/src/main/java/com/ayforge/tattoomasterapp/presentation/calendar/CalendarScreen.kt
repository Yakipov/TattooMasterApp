package com.ayforge.tattoomasterapp.presentation.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ayforge.tattoomasterapp.presentation.appointment.AppointmentViewModel
import com.ayforge.tattoomasterapp.presentation.calendar.components.MonthCalendar
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    viewModel: AppointmentViewModel = koinViewModel(),
    onDayClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val currentMonth = YearMonth.from(today)

    val appointmentsByDay = viewModel.appointmentsForMonth.collectAsState().value

    // Загружаем встречи для текущего месяца при входе
    LaunchedEffect(currentMonth) {
        viewModel.loadAppointmentsForMonth(currentMonth)
    }

    Scaffold(
        topBar = {
            Text(
                text = currentMonth.month.name.lowercase().replaceFirstChar { it.titlecase() } + " " + currentMonth.year,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MonthCalendar(
                selectedDate = today,
                appointmentsByDay = appointmentsByDay,
                onDayClick = { date ->
                    onDayClick(date) // дальше откроем экран дня
                }
            )
        }
    }
}
