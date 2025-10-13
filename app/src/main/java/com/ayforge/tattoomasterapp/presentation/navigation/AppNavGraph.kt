package com.ayforge.tattoomasterapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.presentation.auth.SignInScreen
import com.ayforge.tattoomasterapp.presentation.auth.SignUpScreen
import org.koin.compose.koinInject
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager = koinInject(),
    modifier: Modifier = Modifier,
    startDestinationOverride: String? = null // 👈 добавили параметр
) {
    val isSignedIn = sessionManager.isUserSignedIn

    // Если пользователь авторизован, открываем main (Drawer),
    // иначе — SignIn. Но если пришли из уведомления, учитываем его.
    val startDestination = when {
        !isSignedIn -> "signin"
        startDestinationOverride == "calendar" -> "main" // внутри main — CalendarScreen
        else -> "main"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Auth ---
        composable("signin") {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate("main") {
                        popUpTo("signin") { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate("signup")
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("main") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate("signin")
                }
            )
        }

        // --- Main (Drawer + внутренняя навигация) ---
        composable("main") {
            DrawerScreen(
                navController = navController,
                sessionManager = sessionManager,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                },
                // 👇 сюда можно передать метку, чтобы DrawerScreen знал, что нужно открыть Calendar
                startScreen = startDestinationOverride
            )
        }
    }
}
