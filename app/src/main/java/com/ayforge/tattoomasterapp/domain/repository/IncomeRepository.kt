package com.ayforge.tattoomasterapp.domain.repository

import com.ayforge.tattoomasterapp.domain.model.Income

interface IncomeRepository {
    suspend fun insert(income: Income)
    suspend fun getAll(): List<Income>
    suspend fun getByDate(date: String): List<Income>
    suspend fun deleteById(id: Long)
    suspend fun getByAppointmentId(appointmentId: Long): Income?
}

