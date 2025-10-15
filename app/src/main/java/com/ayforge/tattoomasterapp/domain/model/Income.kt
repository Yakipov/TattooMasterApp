package com.ayforge.tattoomasterapp.domain.model

data class Income(
    val id: Long = 0,
    val appointmentId: Long,
    val amount: Double,
    val paymentMethod: String,
    val note: String? = null,
    val date: Long // timestamp для удобства расчётов
)
