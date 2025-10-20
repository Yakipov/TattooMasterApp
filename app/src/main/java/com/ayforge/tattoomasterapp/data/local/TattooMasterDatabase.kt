package com.ayforge.tattoomasterapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.data.local.entity.IncomeEntity
import com.ayforge.tattoomasterapp.data.local.dao.AppointmentDao
import com.ayforge.tattoomasterapp.data.local.dao.ClientDao
import com.ayforge.tattoomasterapp.data.local.dao.IncomeDao
import com.ayforge.tattoomasterapp.data.local.dao.PaymentMethodDao
import com.ayforge.tattoomasterapp.data.local.entity.PaymentMethodEntity

@Database(
    entities = [
        ClientEntity::class,
        AppointmentEntity::class,
        IncomeEntity::class,
        PaymentMethodEntity::class
    ],
    version = 8, // ← увеличиваем версию
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class TattooMasterDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun incomeDao(): IncomeDao
    abstract fun paymentMethodDao(): PaymentMethodDao

}
