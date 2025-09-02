package com.ayforge.tattoomasterapp.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.room.util.TableInfo
import com.ayforge.tattoomasterapp.R
import com.ayforge.tattoomasterapp.presentation.calendar.components.MonthCalendar
import org.koin.androidx.compose.koinViewModel
import com.ayforge.tattoomasterapp.presentation.user.UserViewModel
import java.time.LocalDate

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MonthCalendar(
            selectedDate = LocalDate.now(),
            onDayClick = { clickedDate ->
                println("Clicked: $clickedDate")
            }
        )
    }
}
