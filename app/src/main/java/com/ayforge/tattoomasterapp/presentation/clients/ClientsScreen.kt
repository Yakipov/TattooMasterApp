package com.ayforge.tattoomasterapp.presentation.clients

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import com.ayforge.tattoomasterapp.presentation.clients.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    navController: NavController,
    viewModel: ClientViewModel = koinViewModel()
) {
    val clients = viewModel.clients.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Клиенты") }
            )
        }
    ) { innerPadding ->
        if (clients.value.isEmpty()) {
            // Заглушка
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Пока нет клиентов")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {
                        // TODO: переход на экран добавления клиента
                        // Например: navController.navigate("create_client")
                    }) {
                        Text("Добавить клиента")
                    }
                }
            }
        } else {
            // Список клиентов
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(clients.value) { client ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("clientDetail/${client.id}")
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(client.name, style = MaterialTheme.typography.titleMedium)
                            Text(client.phone, style = MaterialTheme.typography.bodyMedium)
                            client.email?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
