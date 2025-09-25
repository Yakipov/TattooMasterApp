package com.ayforge.tattoomasterapp.data.repository

import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.data.local.dao.ClientDao
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.data.local.relation.ClientWithAppointments
import com.ayforge.tattoomasterapp.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow

class ClientRepositoryImpl(
    private val clientDao: ClientDao,
    private val sessionManager: SessionManager
) : ClientRepository {

    private fun currentUserId(): String =
        sessionManager.userId ?: throw IllegalStateException("User not signed in")

    override fun getAllClients(): Flow<List<ClientEntity>> =
        clientDao.getAllClients(currentUserId())

    override suspend fun insert(client: ClientEntity): Long =
        clientDao.insert(client.copy(userId = currentUserId()))

    override suspend fun update(client: ClientEntity) =
        clientDao.update(client.copy(userId = currentUserId()))

    override suspend fun delete(client: ClientEntity) =
        clientDao.delete(client.copy(userId = currentUserId()))

    override suspend fun getByNameAndPhone(name: String, phone: String): ClientEntity? =
        clientDao.getByNameAndPhone(name, phone, currentUserId())

    override suspend fun getClientById(id: Long): ClientEntity? =
        clientDao.getClientById(id, currentUserId())

    override fun observeClientById(id: Long): Flow<ClientEntity?> =
        clientDao.observeClientById(id, currentUserId())

    override fun getClientWithAppointments(id: Long): Flow<ClientWithAppointments?> =
        clientDao.getClientWithAppointments(id, currentUserId())

    override suspend fun deleteClientById(clientId: Long) {
        clientDao.deleteClientById(clientId, currentUserId())
    }

    /**
     * Обновление клиента по id: обновляем name, phone и email.
     * Note оставляем без изменений (если надо — можно добавить параметр).
     */
    override suspend fun updateClient(id: Long, name: String, phone: String, email: String?) {
        val userId = currentUserId()
        val existing = clientDao.getClientById(id, userId)
            ?: throw IllegalStateException("Client with id=$id not found for user $userId")

        val updated = existing.copy(
            name = name,
            phone = phone,
            email = email,
            userId = userId
        )

        clientDao.update(updated)
    }
}
