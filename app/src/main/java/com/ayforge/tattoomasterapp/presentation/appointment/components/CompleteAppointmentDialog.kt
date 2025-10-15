package com.ayforge.tattoomasterapp.presentation.appointment.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ayforge.tattoomasterapp.domain.repository.PaymentMethodRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteAppointmentDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double?, String?, String?) -> Unit
) {
    val repo: PaymentMethodRepository = koinInject()
    val scope = rememberCoroutineScope()

    var amount by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var methods by remember { mutableStateOf(listOf<String>()) }
    var expanded by remember { mutableStateOf(false) }

    // Загружаем методы оплаты при открытии
    LaunchedEffect(Unit) {
        methods = repo.getAll()
        if (methods.isNotEmpty() && method.isBlank()) {
            method = methods.first() // авто-подстановка первого
        } else if (methods.isEmpty()) {
            method = "Наличные"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Завершить встречу") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // Сумма
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Сумма (KZT)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Выпадающий список метода оплаты
                if (amount.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = method,
                            onValueChange = { method = it },
                            label = { Text("Метод оплаты") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = false,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            methods.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        method = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Заметка
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Заметка (необязательно)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Основная кнопка
                Button(
                    onClick = {
                        val numericAmount = amount.toDoubleOrNull()
                        onConfirm(numericAmount, method.ifBlank { null }, note)

                        // Сохраняем новый метод, если введён вручную
                        if (method.isNotBlank()) {
                            scope.launch {
                                repo.addIfNew(method.trim())
                            }
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить и завершить")
                }

                // Нижний ряд кнопок (без сдвига)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    TextButton(
                        onClick = {
                            onConfirm(null, null, note)
                            onDismiss()
                        }
                    ) {
                        Text("Завершить без оплаты")
                    }
                }
            }
        }
    )
}
