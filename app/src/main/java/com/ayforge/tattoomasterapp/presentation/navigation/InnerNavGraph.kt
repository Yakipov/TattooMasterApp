package com.ayforge.tattoomasterapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ayforge.tattoomasterapp.presentation.appointment.*
import com.ayforge.tattoomasterapp.presentation.calendar.*
import com.ayforge.tattoomasterapp.presentation.clients.*
import com.ayforge.tattoomasterapp.presentation.income.IncomesScreen
import com.ayforge.tattoomasterapp.presentation.profile.*
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun InnerNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("calendar") {
            CalendarScreen { date ->
                navController.navigate("day/$date")
            }
        }

        composable("clients") {
            ClientsScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // Добавьте сюда composable("notification_settings"), если еще не сделали
        composable("notification_settings") {
            NotificationSettingsScreen(navController = navController)
        }

        composable("incomes") {
            IncomesScreen(navController)
        }

        composable(
            route = "day/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date")
            val date = dateString?.let { LocalDate.parse(it) } ?: LocalDate.now()

            DayScreen(
                navController = navController,
                date = date
            )
        }

        // Маршрут для создания новой встречи (с необязательным временем)
        composable(
            route = "appointment/new?date={date}&time={time}",
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                },
                navArgument("time") {
                    type = NavType.StringType
                    nullable = true // Время может быть не указано
                }
            )
        ) { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date")
            val timeStr = backStackEntry.arguments?.getString("time")

            val date = dateStr?.let { LocalDate.parse(it) } ?: LocalDate.now()
            val time = timeStr?.let { LocalTime.parse(it) }

            CreateAppointmentScreen(
                navController = navController,
                date = date,
                time = time
            )
        }

        // Маршрут для деталей существующей встречи
        composable(
            route = "appointment/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getLong("id") ?: 0L
            AppointmentDetailScreen(
                navController = navController,
                appointmentId = appointmentId
            )
        }
    }
}