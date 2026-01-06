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
import androidx.navigation.NavHostController
import com.ayforge.tattoomasterapp.R
import com.ayforge.tattoomasterapp.presentation.user.UserViewModel
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
    val reminderEnabled by profileViewModel.reminderEnabled.collectAsState()
    val reminderMinutesBefore by profileViewModel.reminderMinutesBefore.collectAsState()

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val availableLanguages = languageViewModel.getAvailableLanguages()

    val context = LocalContext.current
    val activity = context as Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // ---------- Профиль ----------
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

        // ---------- Язык приложения ----------
        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        LanguageSelector(
            currentLanguage = currentLanguage,
            availableLanguages = availableLanguages,
            onLanguageSelected = { lang ->
                languageViewModel.setLanguage(lang, activity)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- Уведомления о встречах ----------
        Text(
            text = "Напоминания о встречах",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Уведомлять о встречах", modifier = Modifier.weight(1f))
            Switch(
                checked = reminderEnabled,
                onCheckedChange = { profileViewModel.setReminderEnabled(it) }
            )
        }

        Button(
            onClick = {
                navController.navigate("notification_settings")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Настройки уведомлений")
        }

        if (reminderEnabled) {
            Spacer(modifier = Modifier.height(12.dp))
            var expanded by remember { mutableStateOf(false) }
            val options = listOf(5, 10, 30, 60, 120)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = "$reminderMinutesBefore минут",
                    onValueChange = {},
                    label = { Text("За сколько минут до встречи") },
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
                    options.forEach { minutes ->
                        DropdownMenuItem(
                            text = { Text("$minutes минут") },
                            onClick = {
                                profileViewModel.setReminderMinutes(minutes)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- Logout ----------
        Button(
            onClick = {
                profileViewModel.logout(context)

                navController.navigate("signin") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.logout))
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
