package com.ayforge.tattoomasterapp.domain.model

import java.time.LocalDateTime

data class IncomeWithClient(
    val id: Long,
    val amount: Double,
    val method: String?,
    val note: String?,
    val createdAt: LocalDateTime,
    val clientName: String?,
    val clientPhone: String?
)
