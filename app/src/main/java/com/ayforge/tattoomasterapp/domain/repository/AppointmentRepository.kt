package com.ayforge.tattoomasterapp.domain.repository

import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentWithClient
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface AppointmentRepository {
    // --- CRUD ---
    suspend fun insert(appointment: AppointmentEntity): Long
    suspend fun update(appointment: AppointmentEntity)
    suspend fun delete(appointment: AppointmentEntity)

    // --- AppointmentEntity ---
    suspend fun getAppointmentById(id: Long): AppointmentEntity?
    fun getAllAppointments(): Flow<List<AppointmentEntity>>
    fun getAppointmentsForClient(clientId: Long): Flow<List<AppointmentEntity>>
    fun getAppointmentsBetween(start: LocalDateTime, end: LocalDateTime): Flow<List<AppointmentEntity>>

    // --- AppointmentWithClient (JOIN) ---
    fun getAppointmentsWithClientBetween(start: LocalDateTime, end: LocalDateTime): Flow<List<AppointmentWithClient>>
    suspend fun getAppointmentWithClientById(id: Long): AppointmentWithClient?

    fun getAppointmentsByClientId(clientId: Long): Flow<List<AppointmentWithClient>>
    suspend fun getAppointmentsByClient(clientId: Long): List<AppointmentWithClient>

    suspend fun deleteAppointmentsByClientId(clientId: Long)



}

