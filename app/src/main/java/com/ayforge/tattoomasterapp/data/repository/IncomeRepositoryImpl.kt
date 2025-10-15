package com.ayforge.tattoomasterapp.data.repository

import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.data.local.dao.IncomeDao
import com.ayforge.tattoomasterapp.data.local.entity.IncomeEntity
import com.ayforge.tattoomasterapp.domain.model.Income
import com.ayforge.tattoomasterapp.domain.repository.IncomeRepository
import java.time.*

class IncomeRepositoryImpl(
    private val incomeDao: IncomeDao,
    private val sessionManager: SessionManager
) : IncomeRepository {

    private fun currentUserId(): String =
        sessionManager.userId ?: throw IllegalStateException("User not signed in")

    // Маппинг: Domain → Entity
    private fun Income.toEntity(userId: String) = IncomeEntity(
        id = id,
        appointmentId = appointmentId,
        amount = amount,
        method = paymentMethod,
        note = note,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneOffset.UTC),
        userId = userId
    )

    // Маппинг: Entity → Domain
    private fun IncomeEntity.toDomain() = Income(
        id = id,
        appointmentId = appointmentId ?: 0,
        amount = amount,
        paymentMethod = method ?: "",
        note = note,
        date = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()
    )

    override suspend fun insert(income: Income) {
        incomeDao.insert(income.toEntity(currentUserId()))
    }

    override suspend fun getAll(): List<Income> {
        return incomeDao.getAllByUser(currentUserId()).map { it.toDomain() }
    }

    override suspend fun getByDate(date: String): List<Income> {
        val userId = currentUserId()
        // Преобразуем строку (yyyy-MM-dd) в диапазон начала/конца дня
        val localDate = LocalDate.parse(date)
        val start = localDate.atStartOfDay()
        val end = localDate.plusDays(1).atStartOfDay()
        return incomeDao.getByDate(userId, start, end).map { it.toDomain() }
    }

    override suspend fun deleteById(id: Long) {
        incomeDao.deleteById(id, currentUserId())
    }

    override suspend fun getByAppointmentId(appointmentId: Long): Income? {
        val all = incomeDao.getAllByUser(currentUserId())
        return all.firstOrNull { it.appointmentId == appointmentId }?.toDomain()
    }
}
