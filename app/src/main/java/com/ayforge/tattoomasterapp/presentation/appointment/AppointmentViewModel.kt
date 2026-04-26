package com.ayforge.tattoomasterapp.presentation.appointment

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.core.notifications.AlarmScheduler
import com.ayforge.tattoomasterapp.core.settings.SettingsDataStore
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentWithClient
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.data.repository.AppointmentRepositoryImpl
import com.ayforge.tattoomasterapp.domain.repository.AppointmentRepository
import com.ayforge.tattoomasterapp.domain.repository.ClientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

// --- ИСПРАВЛЕНИЕ ЗДЕСЬ: ВОЗВРАЩАЕМ ЭТОТ БЛОК ---
sealed class ClientCheckResult {
    data class ExistingClient(val client: ClientEntity) : ClientCheckResult()
    object NewClient : ClientCheckResult()
}
// --- КОНЕЦ ИСПРАВЛЕНИЯ ---

class AppointmentViewModel(
    private val clientRepository: ClientRepository,
    private val appointmentRepository: AppointmentRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // --- ИСПРАВЛЕНИЕ ЗДЕСЬ: ПЕРЕНОСИМ TAG В COMPANION OBJECT ---
    companion object {
        private const val TAG = "AppointmentVM_DEBUG" // Тег для фильтрации
    }
    // --- КОНЕЦ ИСПРАВЛЕНИЯ ---

    private val reminderEnabledFlow = settingsDataStore.reminderEnabled
    private val reminderMinutesFlow = settingsDataStore.reminderMinutesBefore

    private val _clientCheckResult = MutableStateFlow<ClientCheckResult?>(null)
    val clientCheckResult: StateFlow<ClientCheckResult?> = _clientCheckResult

    private val _appointmentsForMonth =
        MutableStateFlow<Map<LocalDate, List<AppointmentEntity>>>(emptyMap())
    val appointmentsForMonth: StateFlow<Map<LocalDate, List<AppointmentEntity>>> =
        _appointmentsForMonth

    private val _appointmentsForDay =
        MutableStateFlow<List<AppointmentWithClient>>(emptyList())
    val appointmentsForDay: StateFlow<List<AppointmentWithClient>> = _appointmentsForDay

    private val _selectedAppointment = MutableStateFlow<AppointmentWithClient?>(null)
    val selectedAppointment: StateFlow<AppointmentWithClient?> =
        _selectedAppointment.asStateFlow()

    // --------------------
    // Client check
    // --------------------
    fun checkClient(name: String, phone: String) {
        viewModelScope.launch {
            val existing = clientRepository.getByNameAndPhone(name.trim(), phone.trim())
            _clientCheckResult.value =
                if (existing != null) ClientCheckResult.ExistingClient(existing)
                else ClientCheckResult.NewClient
        }
    }

    fun clearClientCheck() {
        _clientCheckResult.value = null
    }

    // --------------------
    // Create appointment
    // --------------------
    fun createAppointment(
        client: ClientEntity,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        description: String?,
        context: Context
    ) {
        viewModelScope.launch {

            val clientId =
                if (client.id == 0L) clientRepository.insert(client)
                else client.id

            val appointment = AppointmentEntity(
                clientId = clientId,
                startTime = startTime,
                endTime = endTime,
                description = description,
                userId = ""
            )

            val insertedId = appointmentRepository.insert(appointment)

            scheduleReminderIfNeeded(
                context = context,
                appointmentId = insertedId,
                startTime = startTime,
                description = description
            )

            loadAppointmentsForMonth(YearMonth.from(startTime.toLocalDate()))
            loadAppointmentsForDay(startTime.toLocalDate())
        }
    }

    // --------------------
    // Update appointment
    // --------------------
    fun updateAppointment(
        appointment: AppointmentEntity,
        client: ClientEntity,
        context: Context
    ) {
        viewModelScope.launch {

            val clientId =
                if (client.id == 0L) clientRepository.insert(client)
                else {
                    clientRepository.update(client)
                    client.id
                }

            val apptToSave = appointment.copy(
                clientId = clientId,
                userId = ""
            )

            appointmentRepository.update(apptToSave)

            AlarmScheduler.cancel(context, apptToSave.id)

            scheduleReminderIfNeeded(
                context = context,
                appointmentId = apptToSave.id,
                startTime = apptToSave.startTime,
                description = apptToSave.description
            )

            _selectedAppointment.value =
                appointmentRepository.getAppointmentWithClientById(apptToSave.id)

            loadAppointmentsForMonth(YearMonth.from(apptToSave.startTime.toLocalDate()))
            loadAppointmentsForDay(apptToSave.startTime.toLocalDate())
        }
    }

    // --------------------
    // Delete appointment
    // --------------------
    fun deleteAppointment(
        appointment: AppointmentEntity,
        context: Context
    ) {
        viewModelScope.launch {

            appointmentRepository.delete(appointment)
            _selectedAppointment.value = null

            AlarmScheduler.cancel(context, appointment.id)

            loadAppointmentsForMonth(YearMonth.from(appointment.startTime.toLocalDate()))
            loadAppointmentsForDay(appointment.startTime.toLocalDate())
        }
    }

    // --------------------
    // Loaders
    // --------------------
    fun loadAppointmentsForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val start = yearMonth.atDay(1).atStartOfDay()
            val end = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay()

            appointmentRepository.getAppointmentsBetween(start, end)
                .collectLatest { list ->
                    _appointmentsForMonth.value =
                        list.groupBy { it.startTime.toLocalDate() }
                }
        }
    }

    fun loadAppointmentsForDay(date: LocalDate) {
        viewModelScope.launch {
            val start = date.atStartOfDay()
            val end = date.plusDays(1).atStartOfDay()

            appointmentRepository
                .getAppointmentsWithClientBetween(start, end)
                .collect { list -> _appointmentsForDay.value = list }
        }
    }

    fun loadAppointmentById(appointmentId: Long) {
        viewModelScope.launch {
            _selectedAppointment.value =
                appointmentRepository.getAppointmentWithClientById(appointmentId)
        }
    }

    // --------------------
    // Helpers
    // --------------------
    fun hasOverlap(
        start: LocalDateTime,
        end: LocalDateTime,
        ignoreId: Long? = null
    ): Boolean {
        return appointmentsForDay.value.any {
            val appt = it.appointment
            if (ignoreId != null && appt.id == ignoreId) false
            else start < appt.endTime && end > appt.startTime
        }
    }

    private suspend fun scheduleReminderIfNeeded(
        context: Context,
        appointmentId: Long,
        startTime: LocalDateTime,
        description: String?
    ) {
        val enabled = reminderEnabledFlow.first()
        Log.d(TAG, "Проверка планирования для ID: $appointmentId. Уведомления включены: $enabled")
        if (!enabled) return

        val minutesBefore = reminderMinutesFlow.first()
        Log.d(TAG, "Время до уведомления: $minutesBefore минут")

        val triggerAtMillis = startTime
            .minusMinutes(minutesBefore.toLong())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // Максимально подробный лог времени
        val currentTimeMillis = System.currentTimeMillis()
        Log.d(TAG, "Время встречи: $startTime")
        Log.d(TAG, "Время срабатывания (в миллисекундах): $triggerAtMillis (это ${Date(triggerAtMillis)})")
        Log.d(TAG, "Текущее время (в миллисекундах): $currentTimeMillis (это ${Date(currentTimeMillis)})")

        if (triggerAtMillis > currentTimeMillis) {
            AlarmScheduler.schedule(
                context = context,
                appointmentId = appointmentId,
                triggerAtMillis = triggerAtMillis,
                title = description ?: "Встреча"
            )
        } else {
            Log.w(TAG, "ОШИБКА ПЛАНИРОВАНИЯ: Попытка запланировать уведомление в прошлом. Уведомление НЕ будет установлено.")
        }
    }

    // --------------------
    // Complete appointment
    // --------------------
    fun completeAppointment(
        appointmentId: Long,
        amount: Double?,
        paymentMethod: String?,
        note: String?
    ) {
        viewModelScope.launch {
            (appointmentRepository as? AppointmentRepositoryImpl)
                ?.completeAppointment(
                    appointmentId,
                    amount,
                    paymentMethod,
                    note
                )
        }
    }
}