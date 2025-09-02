package com.ayforge.tattoomasterapp.presentation.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    navController: NavHostController
) {
    val sessionManager: SessionManager = koinInject()
    val firebaseAuth: FirebaseAuth = koinInject()

    LaunchedEffect(Unit) {
        delay(1000L) // небольшая задержка (для анимации, логотипа и т.д.)

        val isLoggedIn = sessionManager.isUserSignedIn && firebaseAuth.currentUser != null

        if (isLoggedIn) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("signin") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Загрузка...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}