package com.ayforge.tattoomasterapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.presentation.auth.SignInScreen
import com.ayforge.tattoomasterapp.presentation.auth.SignUpScreen
import com.ayforge.tattoomasterapp.presentation.navigation.DrawerScreen
import org.koin.compose.koinInject
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager = koinInject(),
    modifier: Modifier = Modifier
) {
    val startDestination = if (sessionManager.isUserSignedIn) "main" else "signin"

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

        // --- Main (Drawer + inner navigation) ---
        composable("main") {
            // передаём наружный navController чтобы DrawerScreen мог при logout'е вернуться на signin
            DrawerScreen(
                navController = navController,
                sessionManager = sessionManager,
                onLogout = {
                    // Очистка Firebase производится внутри DrawerScreen через onLogout,
                    // здесь можем дополнительно делать логику, если нужно.
                    FirebaseAuth.getInstance().signOut()
                }
            )
        }
    }
}
