package com.ayforge.tattoomasterapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.data.local.dao.AppointmentDao
import com.ayforge.tattoomasterapp.data.local.dao.ClientDao


@Database(
    entities = [ClientEntity::class, AppointmentEntity::class],
    version = 2
)
@TypeConverters(DateTimeConverters::class)
abstract class TattooMasterDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun appointmentDao(): AppointmentDao
}
