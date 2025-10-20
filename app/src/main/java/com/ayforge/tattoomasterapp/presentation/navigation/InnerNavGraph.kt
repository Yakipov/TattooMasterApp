package com.ayforge.tattoomasterapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.ayforge.tattoomasterapp.presentation.calendar.CalendarScreen
import com.ayforge.tattoomasterapp.presentation.calendar.DayScreen
import com.ayforge.tattoomasterapp.presentation.appointment.CreateAppointmentScreen
import com.ayforge.tattoomasterapp.presentation.appointment.AppointmentDetailScreen
import com.ayforge.tattoomasterapp.presentation.clients.ClientsScreen
import com.ayforge.tattoomasterapp.presentation.clients.ClientDetailScreen
import com.ayforge.tattoomasterapp.presentation.clients.EditClientScreen
import com.ayforge.tattoomasterapp.presentation.income.IncomesScreen
import com.ayforge.tattoomasterapp.presentation.profile.ProfileScreen
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun InnerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "calendar",
        modifier = modifier
    ) {
        composable("calendar") {
            // CalendarScreen должен использовать переданный коллбек для перехода в день
            CalendarScreen(
                onDayClick = { date ->
                    navController.navigate("day/$date")
                }
            )
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

        // Create appointment (date, optional time)
        composable(
            route = "appointment/new?date={date}&time={time}",
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            val timeStr = backStackEntry.arguments?.getString("time")
            val date = LocalDate.parse(dateStr)
            val time = timeStr?.takeIf { it.isNotEmpty() }?.let { LocalTime.parse(it) }

            CreateAppointmentScreen(
                navController = navController,
                date = date,
                time = time
            )
        }

        // Appointment detail
        composable(
            route = "appointment/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            AppointmentDetailScreen(navController = navController, appointmentId = id)
        }

        // Clients list
        composable("clients") {
            ClientsScreen(navController = navController)
        }

        // Client detail + edit
        composable(
            route = "clientDetail/{clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
            ClientDetailScreen(navController = navController, clientId = clientId)
        }

        composable(
            route = "edit_client/{clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
            EditClientScreen(clientId = clientId, navController = navController)
        }

        // Profile
        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // Incomes
        composable("incomes") {
            IncomesScreen(navController = navController)
        }

    }
}
