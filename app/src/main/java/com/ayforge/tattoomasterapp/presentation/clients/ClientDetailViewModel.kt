package com.ayforge.tattoomasterapp.presentation.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayforge.tattoomasterapp.data.local.relation.ClientWithAppointments
import com.ayforge.tattoomasterapp.domain.repository.AppointmentRepository
import com.ayforge.tattoomasterapp.domain.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ClientDetailViewModel(
    private val clientRepository: ClientRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    private val _clientWithAppointments = MutableStateFlow<ClientWithAppointments?>(null)
    val clientWithAppointments: StateFlow<ClientWithAppointments?> = _clientWithAppointments.asStateFlow()

    fun loadClient(clientId: Long) {
        viewModelScope.launch {
            clientRepository.getClientWithAppointments(clientId).collectLatest { clientWithAppointments ->
                _clientWithAppointments.value = clientWithAppointments
            }
        }
    }

    fun deleteClient(clientId: Long) {
        viewModelScope.launch {
            // сначала удаляем все встречи клиента
            appointmentRepository.deleteAppointmentsByClientId(clientId)
            // потом самого клиента
            clientRepository.deleteClientById(clientId)
        }
    }

    fun updateClient(id: Long, name: String, phone: String, email: String?) {
        viewModelScope.launch {
            clientRepository.updateClient(id, name, phone, email)
        }
    }

}
