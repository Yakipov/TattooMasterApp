package com.ayforge.tattoomasterapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "incomes")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appointmentId: Long,
    val amount: Double,
    val method: String?,
    val note: String?,
    val createdAt: LocalDateTime,
    val userId: String
)