package com.ayforge.tattoomasterapp.presentation.profile

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.ayforge.tattoomasterapp.R
import com.ayforge.tattoomasterapp.presentation.user.UserViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel(),
    languageViewModel: LanguageViewModel = koinViewModel(),
    navController: NavHostController
) {
    val user by profileViewModel.userState.collectAsState()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val availableLanguages = languageViewModel.getAvailableLanguages()

    var darkTheme by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val activity = context as Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // ---------- Блок пользователя ----------
        Text(
            text = stringResource(R.string.profile_section_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.profile_email, user?.email ?: "Гость"),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- Блок настроек ----------
        Text(
            text = stringResource(R.string.settings_section_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Тема
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.settings_theme), modifier = Modifier.weight(1f))
            Switch(
                checked = darkTheme,
                onCheckedChange = { darkTheme = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- Язык ----------
        Text(text = stringResource(R.string.language))
        Spacer(modifier = Modifier.height(8.dp))

        LanguageSelector(
            currentLanguage = currentLanguage,
            availableLanguages = availableLanguages,
            onLanguageSelected = { lang ->
                languageViewModel.setLanguage(lang, activity)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Уведомления
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.settings_notifications), modifier = Modifier.weight(1f))
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- Logout ----------
        Button(
            onClick = {
                profileViewModel.logout()
                navController.navigate("signin") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.logout))
        }

        // ---------- Debug FCM ----------
        Button(
            onClick = {
                userViewModel.saveFcmToken("TEST_TOKEN_UI")
                userViewModel.getFcmToken { token: String? ->
                    android.util.Log.d("ProfileScreen", "FCM token from DataStore = $token")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Показать FCM токен (Debug)")
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    currentLanguage: String,
    availableLanguages: List<Locale>,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = availableLanguages.firstOrNull { it.language == currentLanguage }?.displayLanguage
                ?: currentLanguage.uppercase(),
            onValueChange = {},
            label = { Text(stringResource(R.string.language)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableLanguages.forEach { locale ->
                DropdownMenuItem(
                    text = { Text(locale.displayLanguage) },
                    onClick = {
                        onLanguageSelected(locale.language)
                        expanded = false
                    }
                )
            }
        }
    }
}
