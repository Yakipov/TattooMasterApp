package com.ayforge.tattoomasterapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class IncomeWithClient(
    @Embedded val income: IncomeEntity,

    @Relation(
        parentColumn = "appointmentId",
        entityColumn = "id",
        entity = AppointmentEntity::class
    )
    val appointmentWithClient: AppointmentWithClient? = null
)
