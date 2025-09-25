package com.ayforge.tattoomasterapp.data.local.dao

import androidx.room.*
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentWithClient
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: AppointmentEntity): Long

    @Update
    suspend fun update(appointment: AppointmentEntity)

    @Delete
    suspend fun delete(appointment: AppointmentEntity)

    // --- Только AppointmentEntity ---
    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY startTime DESC")
    fun getAllAppointments(userId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE userId = :userId AND clientId = :clientId ORDER BY startTime DESC")
    fun getAppointmentsForClient(userId: String, clientId: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE userId = :userId AND startTime >= :start AND startTime < :end ORDER BY startTime ASC")
    fun getAppointmentsBetween(
        userId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE userId = :userId AND id = :id")
    suspend fun getAppointmentById(userId: String, id: Long): AppointmentEntity?

    // --- AppointmentWithClient (JOIN) ---
    @Transaction
    @Query("SELECT * FROM appointments WHERE userId = :userId AND startTime >= :start AND startTime < :end ORDER BY startTime ASC")
    fun getAppointmentsWithClientBetween(
        userId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<List<AppointmentWithClient>>

    @Transaction
    @Query("SELECT * FROM appointments WHERE userId = :userId AND id = :id LIMIT 1")
    suspend fun getAppointmentWithClientById(userId: String, id: Long): AppointmentWithClient?

    @Transaction
    @Query("SELECT * FROM appointments WHERE userId = :userId AND clientId = :clientId ORDER BY startTime DESC")
    fun getAppointmentsByClientId(userId: String, clientId: Long): Flow<List<AppointmentWithClient>>

    @Query("DELETE FROM appointments WHERE userId = :userId AND clientId = :clientId")
    suspend fun deleteAppointmentsByClientId(userId: String, clientId: Long)
}
