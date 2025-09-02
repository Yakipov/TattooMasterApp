package com.ayforge.tattoomasterapp.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import com.ayforge.tattoomasterapp.R
import com.ayforge.tattoomasterapp.presentation.user.UserViewModel


@Composable
fun ProfileScreen(
    userViewModel: UserViewModel = koinViewModel(),
    languageViewModel: LanguageViewModel = koinViewModel(),
    navController: NavHostController
) {
    val user by userViewModel.userState.collectAsState()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val availableLanguages = languageViewModel.getAvailableLanguages()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.profile_email, user?.email ?: "Гость"),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.language))
        Box {
            Button(onClick = { expanded = true }) {
                Text(text = currentLanguage)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                availableLanguages.forEach { locale ->
                    DropdownMenuItem(
                        text = { Text(locale.displayLanguage) },
                        onClick = {
                            languageViewModel.setLanguage(locale.language)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            userViewModel.logout()
            navController.navigate("signin") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
            }
        }) {
            Text(text = stringResource(R.string.logout))
        }

    }
}

