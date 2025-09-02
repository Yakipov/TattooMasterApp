package com.ayforge.tattoomasterapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.ayforge.tattoomasterapp.core.session.SessionManager
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

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager = koinInject()
) {
    val startDestination = "calendar" // 👈 теперь начинаем с календаря

    NavHost(
        navController = navController,
        startDestination = startDestination
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

            // 👇 теперь DayScreen сам подтягивает встречи через AppointmentViewModel
            DayScreen(
                navController = navController,
                date = date
            )
        }
        composable(
            route = "appointment/new?date={date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date")
            val date = dateString?.let { LocalDate.parse(it) } ?: LocalDate.now()

            CreateAppointmentScreen(
                navController = navController,
                date = date
            )
        }
        composable(
            route = "appointment/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            AppointmentDetailScreen(
                navController = navController,
                appointmentId = id // 👈 всё остаётся
            )
        }

    }
}