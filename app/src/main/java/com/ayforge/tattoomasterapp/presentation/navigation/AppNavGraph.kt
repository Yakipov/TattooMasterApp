package com.ayforge.tattoomasterapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.presentation.clients.ClientsScreen
import com.ayforge.tattoomasterapp.presentation.appointment.AppointmentDetailScreen
import com.ayforge.tattoomasterapp.presentation.appointment.CreateAppointmentScreen
import com.ayforge.tattoomasterapp.presentation.splash.SplashScreen
import com.ayforge.tattoomasterapp.presentation.auth.SignInScreen
import com.ayforge.tattoomasterapp.presentation.home.HomeScreen
import com.ayforge.tattoomasterapp.presentation.profile.ProfileScreen
import com.ayforge.tattoomasterapp.presentation.calendar.CalendarScreen
import com.ayforge.tattoomasterapp.presentation.calendar.DayScreen
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager = koinInject(),
    modifier: Modifier = Modifier
) {
    val startDestination = "calendar" // 👈 начинаем с календаря

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("signin") {
            SignInScreen(navController = navController)
        }
        composable("home") {
            HomeScreen()
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("calendar") {
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

        // 🔑 теперь поддерживаем параметр time
        composable(
            route = "appointment/new?date={date}&time={time}",
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date")!!
            val timeStr = backStackEntry.arguments?.getString("time")

            val date = LocalDate.parse(dateStr)
            val time = timeStr?.takeIf { it.isNotEmpty() }?.let { LocalTime.parse(it) }

            CreateAppointmentScreen(
                navController = navController,
                date = date,
                time = time
            )
        }

        composable(
            route = "appointment/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            AppointmentDetailScreen(
                navController = navController,
                appointmentId = id
            )
        }

        composable("clients") {
            ClientsScreen()
        }
    }
}
