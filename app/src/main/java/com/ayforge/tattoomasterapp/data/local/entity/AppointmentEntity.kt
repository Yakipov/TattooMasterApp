package com.ayforge.tattoomasterapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val description: String?
)
