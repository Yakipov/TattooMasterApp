package com.ayforge.tattoomasterapp.domain.model

import java.time.LocalDateTime

data class Income(
    val id: Long = 0,
    val appointmentId: Long? = null,
    val amount: Double,
    val paymentMethod: String,
    val note: String? = null,
    val date: LocalDateTime,
    val clientName: String? = null,
    val clientPhone: String? = null,
    val completedAt: Long? = null
)
