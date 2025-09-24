package com.ayforge.tattoomasterapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity

data class ClientWithAppointments(
    @Embedded val client: ClientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "clientId"
    )
    val appointments: List<AppointmentEntity>
)
