package com.ayforge.tattoomasterapp.presentation.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.domain.model.Income
import com.ayforge.tattoomasterapp.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class Period { DAY, WEEK, MONTH }

data class IncomesUiState(
    val incomes: List<Income> = emptyList(),
    val totalAmount: Double = 0.0,
    val period: Period = Period.DAY,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now()
)

class IncomesViewModel(
    private val repo: IncomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIncomes()
    }

    fun setPeriod(period: Period) {
        _uiState.value = _uiState.value.copy(period = period)
        loadIncomes()
    }

    private fun loadIncomes() {
        viewModelScope.launch {
            val (start, end) = when (_uiState.value.period) {
                Period.DAY -> {
                    val today = LocalDate.now()
                    today to today
                }
                Period.WEEK -> {
                    val today = LocalDate.now()
                    val start = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                    val end = start.plusDays(6)
                    start to end
                }
                Period.MONTH -> {
                    val today = LocalDate.now()
                    val start = today.withDayOfMonth(1)
                    val end = start.plusMonths(1).minusDays(1)
                    start to end
                }
            }

            val all = repo.getAll()
            val filtered = all.filter {
                val localDate = it.date.toLocalDate() // теперь напрямую из LocalDateTime
                localDate in start..end
            }


            _uiState.value = _uiState.value.copy(
                incomes = filtered,
                totalAmount = filtered.sumOf { it.amount },
                startDate = start,
                endDate = end
            )
        }
    }
}
