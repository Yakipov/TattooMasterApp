package com.ayforge.tattoomasterapp.data.repository

import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.data.local.dao.AppointmentDao
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentWithClient
import com.ayforge.tattoomasterapp.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

class AppointmentRepositoryImpl(
    private val appointmentDao: AppointmentDao,
    private val sessionManager: SessionManager
) : AppointmentRepository {

    private fun currentUserId(): String =
        sessionManager.userId ?: throw IllegalStateException("User not signed in")

    // --- CRUD ---
    override suspend fun insert(appointment: AppointmentEntity): Long =
        appointmentDao.insert(appointment.copy(userId = currentUserId()))

    override suspend fun update(appointment: AppointmentEntity) =
        appointmentDao.update(appointment.copy(userId = currentUserId()))

    override suspend fun delete(appointment: AppointmentEntity) =
        appointmentDao.delete(appointment.copy(userId = currentUserId()))

    // --- AppointmentEntity ---
    override suspend fun getAppointmentById(id: Long): AppointmentEntity? =
        appointmentDao.getAppointmentById(currentUserId(), id)

    override fun getAllAppointments(): Flow<List<AppointmentEntity>> =
        appointmentDao.getAllAppointments(currentUserId())

    override fun getAppointmentsForClient(clientId: Long): Flow<List<AppointmentEntity>> =
        appointmentDao.getAppointmentsForClient(currentUserId(), clientId)

    override fun getAppointmentsBetween(start: LocalDateTime, end: LocalDateTime): Flow<List<AppointmentEntity>> {
        val userId = sessionManager.userId ?: return flowOf(emptyList())
        return appointmentDao.getAppointmentsBetween(userId, start, end)
    }


    // --- AppointmentWithClient (JOIN) ---
    override fun getAppointmentsWithClientBetween(start: LocalDateTime, end: LocalDateTime): Flow<List<AppointmentWithClient>> =
        appointmentDao.getAppointmentsWithClientBetween(currentUserId(), start, end)

    override suspend fun getAppointmentWithClientById(id: Long): AppointmentWithClient? =
        appointmentDao.getAppointmentWithClientById(currentUserId(), id)

    override fun getAppointmentsByClientId(clientId: Long): Flow<List<AppointmentWithClient>> =
        appointmentDao.getAppointmentsByClientId(currentUserId(), clientId)

    // --- Реализация suspend-метода (требуемого интерфейсом) ---
    override suspend fun getAppointmentsByClient(clientId: Long): List<AppointmentWithClient> =
        appointmentDao.getAppointmentsByClientId(currentUserId(), clientId).first()

    override suspend fun deleteAppointmentsByClientId(clientId: Long) {
        appointmentDao.deleteAppointmentsByClientId(currentUserId(), clientId)
    }
}
