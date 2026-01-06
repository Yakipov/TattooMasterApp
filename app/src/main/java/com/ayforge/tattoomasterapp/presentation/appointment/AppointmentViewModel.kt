package com.ayforge.tattoomasterapp.presentation.appointment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.core.notifications.AppointmentNotificationScheduler
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
import kotlinx.coroutines.flow.first

sealed class ClientCheckResult {
    data class ExistingClient(val client: ClientEntity) : ClientCheckResult()
    object NewClient : ClientCheckResult()
}

class AppointmentViewModel(
    private val clientRepository: ClientRepository,
    private val appointmentRepository: AppointmentRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {


    private val reminderEnabledFlow = settingsDataStore.reminderEnabled
    private val reminderMinutesFlow = settingsDataStore.reminderMinutesBefore


    private val _clientCheckResult = MutableStateFlow<ClientCheckResult?>(null)
    val clientCheckResult: StateFlow<ClientCheckResult?> = _clientCheckResult

    private val _appointmentsForMonth =
        MutableStateFlow<Map<LocalDate, List<AppointmentEntity>>>(emptyMap())
    val appointmentsForMonth: StateFlow<Map<LocalDate, List<AppointmentEntity>>> = _appointmentsForMonth

    private val _appointmentsForDay =
        MutableStateFlow<List<AppointmentWithClient>>(emptyList())
    val appointmentsForDay: StateFlow<List<AppointmentWithClient>> = _appointmentsForDay

    private val _selectedAppointment = MutableStateFlow<AppointmentWithClient?>(null)
    val selectedAppointment: StateFlow<AppointmentWithClient?> = _selectedAppointment.asStateFlow()

    fun checkClient(name: String, phone: String) {
        viewModelScope.launch {
            val existing = clientRepository.getByNameAndPhone(name.trim(), phone.trim())
            _clientCheckResult.value = if (existing != null) {
                ClientCheckResult.ExistingClient(existing)
            } else {
                ClientCheckResult.NewClient
            }
        }
    }

    fun createAppointment(
        client: ClientEntity,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        description: String?,
        context: Context
    ) {
        viewModelScope.launch {

            val clientId = if (client.id == 0L) {
                clientRepository.insert(client)
            } else {
                client.id
            }

            val appointment = AppointmentEntity(
                clientId = clientId,
                startTime = startTime,
                endTime = endTime,
                description = description,
                userId = "" // временно
            )

            val insertedId = appointmentRepository.insert(appointment)

            //  читаем настройки
            val reminderEnabled = reminderEnabledFlow.first()
            val minutesBefore = reminderMinutesFlow.first()

            //  планируем уведомление
            if (reminderEnabled) {
                AppointmentNotificationScheduler.scheduleReminder(
                    context = context,
                    appointmentId = insertedId,
                    appointmentTitle = description ?: "Без описания",
                    startTimeMillis = startTime
                        .atZone(java.time.ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    minutesBefore = minutesBefore
                )
            }

            // обновляем UI
            loadAppointmentsForMonth(YearMonth.from(startTime.toLocalDate()))
            loadAppointmentsForDay(startTime.toLocalDate())
        }
    }


    fun deleteAppointment(appointment: AppointmentEntity, context: Context) {
        viewModelScope.launch {
            appointmentRepository.delete(appointment)
            _selectedAppointment.value = null

            // отменяем уведомление
            AppointmentNotificationScheduler.cancelReminder(context, appointment.id)

            // обновляем месяц и день
            loadAppointmentsForMonth(YearMonth.from(appointment.startTime.toLocalDate()))
            loadAppointmentsForDay(appointment.startTime.toLocalDate())
        }
    }


    fun clearClientCheck() {
        _clientCheckResult.value = null
    }

    fun loadAppointmentsForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val start = yearMonth.atDay(1).atStartOfDay()
            val end = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay()

            appointmentRepository.getAppointmentsBetween(start, end)
                .collectLatest { list ->
                    _appointmentsForMonth.value = list.groupBy { it.startTime.toLocalDate() }
                }
        }
    }


    fun loadAppointmentsForDay(date: LocalDate) {
        viewModelScope.launch {
            val start = date.atStartOfDay()
            val end = date.plusDays(1).atStartOfDay()

            appointmentRepository.getAppointmentsWithClientBetween(start, end).collect { list ->
                _appointmentsForDay.value = list
            }
        }
    }

    fun loadAppointmentById(appointmentId: Long) {
        viewModelScope.launch {
            _selectedAppointment.value = appointmentRepository.getAppointmentWithClientById(appointmentId)
        }
    }

    fun updateAppointment(
        appointment: AppointmentEntity,
        client: ClientEntity,
        context: Context
    ) {
        viewModelScope.launch {

            val clientId = if (client.id == 0L) {
                clientRepository.insert(client)
            } else {
                clientRepository.update(client)
                client.id
            }

            val apptToSave = appointment.copy(
                clientId = clientId,
                userId = ""
            )

            if (apptToSave.id == 0L) {
                appointmentRepository.insert(apptToSave)
            } else {
                appointmentRepository.update(apptToSave)
            }

            // отменяем старое уведомление
            AppointmentNotificationScheduler.cancelReminder(
                context = context,
                appointmentId = apptToSave.id
            )

            // ⚙ читаем настройки
            val reminderEnabled = reminderEnabledFlow.first()
            val minutesBefore = reminderMinutesFlow.first()

            // создаём новое уведомление
            if (reminderEnabled) {
                AppointmentNotificationScheduler.scheduleReminder(
                    context = context,
                    appointmentId = apptToSave.id,
                    appointmentTitle = apptToSave.description ?: "Без описания",
                    startTimeMillis = apptToSave.startTime
                        .atZone(java.time.ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    minutesBefore = minutesBefore
                )
            }

            // обновляем UI
            _selectedAppointment.value =
                appointmentRepository.getAppointmentWithClientById(apptToSave.id)

            loadAppointmentsForMonth(YearMonth.from(apptToSave.startTime.toLocalDate()))
            loadAppointmentsForDay(apptToSave.startTime.toLocalDate())
        }
    }


    fun hasOverlap(start: LocalDateTime, end: LocalDateTime, ignoreId: Long? = null): Boolean {
        val list = appointmentsForDay.value
        return list.any { apptWithClient ->
            val appt = apptWithClient.appointment
            if (ignoreId != null && appt.id == ignoreId) return@any false
            start < appt.endTime && end > appt.startTime
        }
    }

    fun completeAppointment(
        appointmentId: Long,
        amount: Double?,
        paymentMethod: String?,
        note: String?
    ) {
        viewModelScope.launch {
            (appointmentRepository as? AppointmentRepositoryImpl)?.completeAppointment(
                appointmentId = appointmentId,
                amount = amount,
                paymentMethod = paymentMethod,
                note = note
            )
        }
    }

}
