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


private const val MAX_BARS = 4

@Composable
private fun loadColor(appointmentsCount: Int): Color {
    return when {
        appointmentsCount >= 7 -> Color(0xFFD32F2F) // красный
        appointmentsCount >= 5 -> Color(0xFFFBC02D) // жёлтый
        appointmentsCount >= 3 -> Color(0xFF388E3C) // зелёный
        appointmentsCount >= 1 -> Color(0xFF1976D2) // синий
        else -> Color.Transparent
    }
}


@Composable
fun MonthCalendar(
    selectedDate: LocalDate = LocalDate.now(),
    appointmentsByDay: Map<LocalDate, List<AppointmentEntity>> = emptyMap(),
    onDayClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val firstOfMonth = selectedDate.withDayOfMonth(1)
    val yearMonth = YearMonth.from(firstOfMonth)

    // Понедельник = 0, воскресенье = 6
    val offset = (firstOfMonth.dayOfWeek.value + 6) % 7

    // 6 недель * 7 дней — как в Google
    val totalDays = 42
    val startDate = firstOfMonth.minusDays(offset.toLong())
    val dates = List(totalDays) { startDate.plusDays(it.toLong()) }



    Column(modifier = Modifier.fillMaxSize()) {

        // Заголовки дней недели
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
            ).forEach { day ->
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

// полоски загрузки
                        if (appointments.isNotEmpty()) {
                            val barsCount = minOf(appointments.size, MAX_BARS)
                            val color = loadColor(appointments.size)

                            // --- НАЧАЛО ИЗМЕНЕНИЙ ---

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter) // Прижимаем к низу ячейки
                                    .fillMaxWidth()              // 👈 1. ЗАСТАВЛЯЕМ КОЛОНКУ РАСТЯНУТЬСЯ НА ВСЮ ШИРИНУ
                                    .padding(bottom = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Bottom),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                repeat(barsCount) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp) // Можно сделать чуть выше для лучшего эффекта
                                            .background(
                                                color = color,
                                                shape = CircleShape // 👈 ИЗМЕНЕНИЕ ЗДЕСЬ
                                            )
                                    )
                                }
                            }
                            // --- КОНЕЦ ИЗМЕНЕНИЙ ---
                        }
                    }
                }
            }
        }
    }
}
