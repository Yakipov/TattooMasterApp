package com.ayforge.tattoomasterapp.data.repository

import com.ayforge.tattoomasterapp.data.local.dao.AppointmentDao
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentWithClient
import com.ayforge.tattoomasterapp.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class AppointmentRepositoryImpl(
    private val appointmentDao: AppointmentDao
) : AppointmentRepository {

    // --- CRUD ---
    override suspend fun insert(appointment: AppointmentEntity): Long =
        appointmentDao.insert(appointment)

    override suspend fun update(appointment: AppointmentEntity) =
        appointmentDao.update(appointment)

    override suspend fun delete(appointment: AppointmentEntity) =
        appointmentDao.delete(appointment)

    // --- AppointmentEntity ---
    override suspend fun getAppointmentById(id: Long): AppointmentEntity? =
        appointmentDao.getAppointmentById(id)

    override fun getAllAppointments(): Flow<List<AppointmentEntity>> =
        appointmentDao.getAllAppointments()

    override fun getAppointmentsForClient(clientId: Long): Flow<List<AppointmentEntity>> =
        appointmentDao.getAppointmentsForClient(clientId)

    override fun getAppointmentsBetween(start: LocalDateTime, end: LocalDateTime): Flow<List<AppointmentEntity>> =
        appointmentDao.getAppointmentsBetween(start, end)

    // --- AppointmentWithClient (JOIN) ---
    override fun getAppointmentsWithClientBetween(start: LocalDateTime, end: LocalDateTime): Flow<List<AppointmentWithClient>> =
        appointmentDao.getAppointmentsWithClientBetween(start, end)

    override suspend fun getAppointmentWithClientById(id: Long): AppointmentWithClient? =
        appointmentDao.getAppointmentWithClientById(id)
}
