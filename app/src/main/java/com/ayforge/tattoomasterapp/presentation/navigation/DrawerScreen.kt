@file:OptIn(ExperimentalMaterial3Api::class)
package com.ayforge.tattoomasterapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ayforge.tattoomasterapp.core.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun DrawerScreen(
    navController: NavHostController,
    sessionManager: SessionManager,
    onLogout: () -> Unit,
    startScreen: String? = null
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val innerNavController = rememberNavController()

    val items = listOf(
        DrawerItem("calendar", "Календарь", Icons.Filled.CalendarToday),
        DrawerItem("incomes", "Доходы", Icons.Filled.AttachMoney),
        DrawerItem("clients", "Клиенты", Icons.Filled.People),
        DrawerItem("profile", "Профиль", Icons.Filled.Person),
        DrawerItem("logout", "Выход", Icons.Filled.Logout)
    )

    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when {
        currentRoute == "calendar" -> "Календарь"
        currentRoute == "profile" -> "Профиль"
        currentRoute == "clients" -> "Клиенты"
        else -> "TattooMasterApp"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = item.route == currentRoute,
                        onClick = {
                            when (item.route) {
                                "logout" -> {
                                    onLogout()
                                    sessionManager.clearSession()
                                    navController.navigate("signin") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                                else -> {
                                    innerNavController.navigate(item.route) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Меню")
                        }
                    }
                )
            }
        ) { innerPadding ->
            InnerNavGraph(
                navController = innerNavController,
                startDestination = startScreen ?: "calendar",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

data class DrawerItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
