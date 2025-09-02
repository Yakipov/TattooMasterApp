package com.ayforge.tattoomasterapp.data.repository

import com.ayforge.tattoomasterapp.data.local.dao.ClientDao
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow

class ClientRepositoryImpl(
    private val dao: ClientDao
) : ClientRepository {

    override suspend fun insert(client: ClientEntity): Long = dao.insert(client)

    override suspend fun update(client: ClientEntity) = dao.update(client)

    override suspend fun delete(client: ClientEntity) = dao.delete(client)

    override suspend fun getAllClients(): Flow<List<ClientEntity>> = dao.getAllClients()

    override suspend fun getByNameAndPhone(name: String, phone: String): ClientEntity? =
        dao.getByNameAndPhone(name, phone)

    override suspend fun getClientById(id: Long): ClientEntity? =
        dao.getClientById(id)
}
