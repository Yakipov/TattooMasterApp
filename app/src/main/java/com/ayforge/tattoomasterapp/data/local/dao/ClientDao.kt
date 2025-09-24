package com.ayforge.tattoomasterapp.data.local.dao

import androidx.room.*
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.data.local.relation.ClientWithAppointments
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(client: ClientEntity): Long

    @Update
    suspend fun update(client: ClientEntity)

    @Delete
    suspend fun delete(client: ClientEntity)

    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE phone = :phone AND name = :name LIMIT 1")
    suspend fun getByNameAndPhone(name: String, phone: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun getClientById(id: Long): ClientEntity?

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    fun observeClientById(id: Long): Flow<ClientEntity?>

    @Transaction
    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    fun getClientWithAppointments(id: Long): Flow<ClientWithAppointments?>

    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteClientById(clientId: Long)

    @Query("UPDATE clients SET name = :name, phone = :phone WHERE id = :id")
    suspend fun updateClient(id: Long, name: String, phone: String)
}
