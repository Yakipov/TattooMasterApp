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
    @Query("SELECT * FROM appointments ORDER BY startTime DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE clientId = :clientId ORDER BY startTime DESC")
    fun getAppointmentsForClient(clientId: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE startTime >= :start AND startTime < :end ORDER BY startTime ASC")
    fun getAppointmentsBetween(
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: Long): AppointmentEntity?

    // --- AppointmentWithClient (JOIN) ---
    @Transaction
    @Query("SELECT * FROM appointments WHERE startTime >= :start AND startTime < :end ORDER BY startTime ASC")
    fun getAppointmentsWithClientBetween(
        start: LocalDateTime,
        end: LocalDateTime
    ): Flow<List<AppointmentWithClient>>

    @Transaction
    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun getAppointmentWithClientById(id: Long): AppointmentWithClient?

    @Transaction
    @Query("SELECT * FROM appointments WHERE clientId = :clientId ORDER BY startTime DESC")
    fun getAppointmentsByClientId(clientId: Long): Flow<List<AppointmentWithClient>>

    @Query("DELETE FROM appointments WHERE clientId = :clientId")
    suspend fun deleteAppointmentsByClientId(clientId: Long)

}
