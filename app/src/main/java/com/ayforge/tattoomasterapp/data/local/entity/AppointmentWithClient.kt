package com.ayforge.tattoomasterapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AppointmentWithClient(
    @Embedded val appointment: AppointmentEntity,
    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: ClientEntity
)
