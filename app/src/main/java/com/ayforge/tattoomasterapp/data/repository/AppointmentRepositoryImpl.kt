package com.ayforge.tattoomasterapp.data.repository

import com.ayforge.tattoomasterapp.data.local.dao.AppointmentDao
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentWithClient
import com.ayforge.tattoomasterapp.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override fun getAppointmentsByClientId(clientId: Long): Flow<List<AppointmentWithClient>> =
        appointmentDao.getAppointmentsByClientId(clientId)

    // --- Реализация suspend-метода (требуемого интерфейсом) ---
    // Возвращаем одноразовый список, взятый из Flow (первое эмит-значение).
    override suspend fun getAppointmentsByClient(clientId: Long): List<AppointmentWithClient> =
        appointmentDao.getAppointmentsByClientId(clientId).first()

    override suspend fun deleteAppointmentsByClientId(clientId: Long) {
        appointmentDao.deleteAppointmentsByClientId(clientId)
    }

}
