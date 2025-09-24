package com.ayforge.tattoomasterapp.domain.repository

import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.data.local.relation.ClientWithAppointments
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    // --- CRUD ---
    suspend fun insert(client: ClientEntity): Long
    suspend fun update(client: ClientEntity)
    suspend fun delete(client: ClientEntity)

    // --- Queries ---
    fun getAllClients(): Flow<List<ClientEntity>>
    suspend fun getByNameAndPhone(name: String, phone: String): ClientEntity?
    suspend fun getClientById(id: Long): ClientEntity?

    // --- Extra (для профиля клиента, с автообновлением) ---
    fun observeClientById(id: Long): Flow<ClientEntity?>

    // 🔹 Исправлено: без suspend
    fun getClientWithAppointments(id: Long): Flow<ClientWithAppointments?>

    suspend fun deleteClientById(clientId: Long)
    suspend fun updateClient(id: Long, name: String, phone: String, email: String?)

}
