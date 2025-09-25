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

    // теперь фильтруем по userId
    @Query("SELECT * FROM clients WHERE userId = :userId ORDER BY name ASC")
    fun getAllClients(userId: String): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE phone = :phone AND name = :name AND userId = :userId LIMIT 1")
    suspend fun getByNameAndPhone(name: String, phone: String, userId: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getClientById(id: Long, userId: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE id = :id AND userId = :userId LIMIT 1")
    fun observeClientById(id: Long, userId: String): Flow<ClientEntity?>

    @Transaction
    @Query("SELECT * FROM clients WHERE id = :id AND userId = :userId LIMIT 1")
    fun getClientWithAppointments(id: Long, userId: String): Flow<ClientWithAppointments?>

    @Query("DELETE FROM clients WHERE id = :clientId AND userId = :userId")
    suspend fun deleteClientById(clientId: Long, userId: String)

    @Query("UPDATE clients SET name = :name, phone = :phone WHERE id = :id AND userId = :userId")
    suspend fun updateClient(id: Long, name: String, phone: String, userId: String)
}
