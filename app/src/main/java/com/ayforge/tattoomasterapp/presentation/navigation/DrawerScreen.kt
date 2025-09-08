@file:OptIn(ExperimentalMaterial3Api::class)
package com.ayforge.tattoomasterapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
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
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun DrawerScreen(
    navController: NavHostController = rememberNavController(),
    onLogout: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf(
        DrawerItem("calendar", "Календарь", Icons.Filled.CalendarToday),
        DrawerItem("profile", "Профиль", Icons.Filled.Person),
        DrawerItem("clients", "Клиенты", Icons.Filled.People),
        DrawerItem("logout", "Выход", Icons.Filled.Logout)
    )

    var selectedItem by remember { mutableStateOf(items.first()) }

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
                                "logout" -> onLogout()
                                else -> navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
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
                    title = { Text(selectedItem.label) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Меню")
                        }
                    }
                )
            }
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
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
