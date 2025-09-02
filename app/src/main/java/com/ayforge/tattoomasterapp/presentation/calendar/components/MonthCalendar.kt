package com.ayforge.tattoomasterapp.presentation.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun MonthCalendar(
    selectedDate: LocalDate = LocalDate.now(),
    appointmentsByDay: Map<LocalDate, List<AppointmentEntity>> = emptyMap(),
    onDayClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val firstOfMonth = selectedDate.withDayOfMonth(1)
    val yearMonth = YearMonth.from(firstOfMonth)

    // Понедельник — первый день
    val firstDayOfWeek = firstOfMonth.dayOfWeek.value.let { if (it == 7) 0 else it }
    val offset = if (firstDayOfWeek == 0) 6 else firstDayOfWeek - 1

    // 6 недель * 7 дней — как в Google
    val totalDays = 42
    val startDate = firstOfMonth.minusDays(offset.toLong())
    val dates = List(totalDays) { startDate.plusDays(it.toLong()) }

    Column(modifier = Modifier.fillMaxSize()) {

        // Заголовки дней недели
        Row(modifier = Modifier.fillMaxWidth()) {
            DayOfWeek.values().drop(1).plus(DayOfWeek.SUNDAY).forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Сетка: без внутренних отступов и без Surface (чтобы не было "плиток")
        dates.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // все 6 строк делят высоту поровну
            ) {
                week.forEach { date ->
                    val isToday = date == today
                    val isCurrentMonth = date.month == selectedDate.month
                    val appointments = appointmentsByDay[date].orEmpty()

                    Box(
                        modifier = Modifier
                            .weight(1f)               // равная ширина
                            .fillMaxHeight()          // на всю высоту строки
                            .border(                   // тонкая линия сетки
                                width = 0.5.dp,
                                color = Color(0xFFE0E0E0),
                                shape = RectangleShape
                            )
                            .clickable { onDayClick(date) },
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // Число дня
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                !isCurrentMonth -> Color.Gray
                                isToday -> MaterialTheme.colorScheme.primary
                                else -> Color.Unspecified
                            },
                            modifier = Modifier.padding(top = 6.dp)
                        )

                        // Маленькая точка-индикатор встреч, как в Google
                        if (appointments.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 6.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
