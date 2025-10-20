package com.ayforge.tattoomasterapp.data.repository

import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.data.local.dao.IncomeDao
import com.ayforge.tattoomasterapp.data.local.entity.IncomeEntity
import com.ayforge.tattoomasterapp.data.local.entity.IncomeWithClient
import com.ayforge.tattoomasterapp.domain.model.Income
import com.ayforge.tattoomasterapp.domain.repository.IncomeRepository
import java.time.*

class IncomeRepositoryImpl(
    private val incomeDao: IncomeDao,
    private val sessionManager: SessionManager
) : IncomeRepository {

    private fun currentUserId(): String =
        sessionManager.userId ?: throw IllegalStateException("User not signed in")

    // ---------- Маппинг ----------
    private fun Income.toEntity(userId: String) = IncomeEntity(
        id = id,
        appointmentId = appointmentId ?: 0,
        amount = amount,
        method = paymentMethod,
        note = note,
        createdAt = date, // теперь тип совпадает (LocalDateTime)
        userId = userId
    )

    private fun IncomeWithClient.toDomain(): Income {
        val income = this.income
        val appointment = this.appointmentWithClient?.appointment
        val client = this.appointmentWithClient?.client

        return Income(
            id = income.id,
            appointmentId = income.appointmentId,
            amount = income.amount,
            paymentMethod = income.method ?: "",
            note = income.note,
            date = income.createdAt, // LocalDateTime напрямую
            clientName = client?.name,
            clientPhone = client?.phone,
            completedAt = appointment?.endTime?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
    }

    // ---------- CRUD ----------
    override suspend fun insert(income: Income) {
        incomeDao.insert(income.toEntity(currentUserId()))
    }

    override suspend fun getAll(): List<Income> {
        return incomeDao.getAllByUser(currentUserId()).map { it.toDomain() }
    }

    override suspend fun getByDate(date: String): List<Income> {
        val userId = currentUserId()
        val localDate = LocalDate.parse(date)
        val start = localDate.atStartOfDay()
        val end = localDate.plusDays(1).atStartOfDay()
        return incomeDao.getByPeriod(userId, start, end).map { it.toDomain() }
    }

    override suspend fun deleteById(id: Long) {
        incomeDao.deleteById(id, currentUserId())
    }

    override suspend fun getByAppointmentId(appointmentId: Long): Income? {
        return incomeDao.getAllByUser(currentUserId())
            .firstOrNull { it.income.appointmentId == appointmentId }
            ?.toDomain()
    }
}
