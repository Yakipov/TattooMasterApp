package com.ayforge.tattoomasterapp.presentation.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentWithClient
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.domain.repository.AppointmentRepository
import com.ayforge.tattoomasterapp.domain.repository.ClientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

sealed class ClientCheckResult {
    data class ExistingClient(val client: ClientEntity) : ClientCheckResult()
    object NewClient : ClientCheckResult()
}

class AppointmentViewModel(
    private val clientRepository: ClientRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    private val _clientCheckResult = MutableStateFlow<ClientCheckResult?>(null)
    val clientCheckResult: StateFlow<ClientCheckResult?> = _clientCheckResult

    private val _appointmentsForMonth =
        MutableStateFlow<Map<LocalDate, List<AppointmentEntity>>>(emptyMap())
    val appointmentsForMonth: StateFlow<Map<LocalDate, List<AppointmentEntity>>> = _appointmentsForMonth

    // теперь встречи с клиентом для дня
    private val _appointmentsForDay =
        MutableStateFlow<List<AppointmentWithClient>>(emptyList())
    val appointmentsForDay: StateFlow<List<AppointmentWithClient>> = _appointmentsForDay

    // одна выбранная встреча с клиентом
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
        description: String?
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
                description = description
            )

            appointmentRepository.insert(appointment)

            // обновляем список
            loadAppointmentsForMonth(YearMonth.from(startTime.toLocalDate()))
            loadAppointmentsForDay(startTime.toLocalDate())
        }
    }

    fun deleteAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            appointmentRepository.delete(appointment)
            _selectedAppointment.value = null
        }
    }

    fun clearClientCheck() {
        _clientCheckResult.value = null
    }

    fun loadAppointmentsForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val start = yearMonth.atDay(1).atStartOfDay()
            val end = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay()

            appointmentRepository.getAppointmentsBetween(start, end).collect { list ->
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
        client: ClientEntity
    ) {
        viewModelScope.launch {
            val clientId = if (client.id == 0L) {
                clientRepository.insert(client)
            } else {
                clientRepository.update(client)
                client.id
            }

            val apptToSave = appointment.copy(clientId = clientId)

            if (apptToSave.id == 0L) {
                appointmentRepository.insert(apptToSave)
            } else {
                appointmentRepository.update(apptToSave)
            }

            // обновляем selected и списки
            _selectedAppointment.value = appointmentRepository.getAppointmentWithClientById(apptToSave.id)
        }
    }

    fun loadAppointmentWithClientById(id: Long) {
        viewModelScope.launch {
            _selectedAppointment.value = appointmentRepository.getAppointmentWithClientById(id)
        }
    }
}
