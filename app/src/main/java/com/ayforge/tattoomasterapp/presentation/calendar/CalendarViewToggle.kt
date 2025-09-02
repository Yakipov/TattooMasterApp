package com.ayforge.tattoomasterapp.presentation.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ayforge.tattoomasterapp.presentation.calendar.components.MonthCalendar
import java.time.LocalDate

@Composable
fun CalendarViewToggle(
    selectedView: CalendarViewType,
    onViewSelected: (CalendarViewType) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        CalendarViewType.values().forEach { type ->
            FilterChip(
                selected = selectedView == type,
                onClick = { onViewSelected(type) },
                label = { Text(type.label) }
            )
        }
    }
    when (selectedView) {
        CalendarViewType.MONTH -> {
            MonthCalendar(
                selectedDate = LocalDate.now(),
                onDayClick = { date ->
                    println("Нажат день: $date")
                    // позже откроем встречи в этот день
                }
            )
        }
        else -> {
            Text("В разработке")
        }
    }
}
