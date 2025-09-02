package com.ayforge.tattoomasterapp.domain.repository

import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    suspend fun insert(client: ClientEntity): Long
    suspend fun update(client: ClientEntity)
    suspend fun delete(client: ClientEntity)
    suspend fun getAllClients(): Flow<List<ClientEntity>>
    suspend fun getByNameAndPhone(name: String, phone: String): ClientEntity?
    suspend fun getClientById(id: Long): ClientEntity?
}

