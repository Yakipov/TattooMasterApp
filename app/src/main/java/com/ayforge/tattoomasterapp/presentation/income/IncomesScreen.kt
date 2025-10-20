package com.ayforge.tattoomasterapp.presentation.income

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayforge.tattoomasterapp.domain.model.Income
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomesScreen(
    navController: NavController,
    viewModel: IncomesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    var selectedIncome by remember { mutableStateOf<Income?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Доходы") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Переключатели периодов
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PeriodButton("День", uiState.period == Period.DAY) { viewModel.setPeriod(Period.DAY) }
                PeriodButton("Неделя", uiState.period == Period.WEEK) { viewModel.setPeriod(Period.WEEK) }
                PeriodButton("Месяц", uiState.period == Period.MONTH) { viewModel.setPeriod(Period.MONTH) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Общая сумма
            Text("Итого: ${uiState.totalAmount} ₸", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Период: ${uiState.startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} — " +
                        uiState.endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Список доходов
            if (uiState.incomes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет данных за этот период")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.incomes) { income ->
                        IncomeListItem(income = income, onClick = { selectedIncome = income })
                    }
                }
            }
        }

        // Диалог деталей дохода
        selectedIncome?.let { income ->
            IncomeDetailDialog(income = income, onDismiss = { selectedIncome = null })
        }
    }
}

@Composable
private fun PeriodButton(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(text) })
}

@Composable
private fun IncomeListItem(income: Income, onClick: () -> Unit) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Сумма: ${income.amount} ₸", style = MaterialTheme.typography.bodyLarge)
            Text("Клиент: ${income.clientName ?: "Без имени"}", style = MaterialTheme.typography.bodyMedium)
            Text("Дата: ${income.date.format(dateFormatter)}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun IncomeDetailDialog(income: Income, onDismiss: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    val dateText = income.date.format(formatter)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Детали дохода") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Сумма: ${income.amount} ₸")
                Text("Метод: ${income.paymentMethod}")
                income.note?.let { if (it.isNotBlank()) Text("Заметка: $it") }
                Text("Клиент: ${income.clientName ?: "Без имени"}")
                Text("Телефон: ${income.clientPhone ?: "-"}")
                Text("Дата: $dateText")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        }
    )
}
