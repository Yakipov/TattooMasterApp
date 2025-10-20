@file:OptIn(ExperimentalMaterial3Api::class)
package com.ayforge.tattoomasterapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
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
    navController: NavHostController, // внешний (AppNavGraph)
    sessionManager: SessionManager,
    onLogout: () -> Unit,
    startScreen: String? = null // 👈 добавили параметр
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val innerNavController = rememberNavController()

    val items = listOf(
        DrawerItem("calendar", "Календарь", Icons.Filled.CalendarToday),
        DrawerItem("incomes", "Доходы", Icons.Filled.AttachMoney), // 🟢 добавили сюда
        DrawerItem("clients", "Клиенты", Icons.Filled.People),
        DrawerItem("profile", "Профиль", Icons.Filled.Person),
        DrawerItem("logout", "Выход", Icons.Filled.Logout)
    )


    var selectedItem by remember { mutableStateOf(items.first()) }

    // 👇 Переходим на нужный экран, если указано при запуске
    LaunchedEffect(startScreen) {
        startScreen?.let {
            innerNavController.navigate(it) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            selectedItem = items.find { item -> item.route == it } ?: items.first()
        }
    }

    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when {
        currentRoute == "calendar" -> "Календарь"
        currentRoute == "profile" -> "Профиль"
        currentRoute == "clients" -> "Клиенты"
        currentRoute?.startsWith("day") == true -> "День"
        currentRoute?.startsWith("appointment") == true -> "Встреча"
        currentRoute?.startsWith("clientDetail") == true -> "Клиент"
        currentRoute?.startsWith("edit_client") == true -> "Редактирование клиента"
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
                        selected = item == selectedItem,
                        onClick = {
                            selectedItem = item
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
                                        popUpTo(innerNavController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Меню")
                        }
                    }
                )
            }
        ) { innerPadding ->
            InnerNavGraph(
                navController = innerNavController,
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
