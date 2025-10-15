package com.ayforge.tattoomasterapp.data.local.dao

import androidx.room.*
import com.ayforge.tattoomasterapp.data.local.entity.PaymentMethodEntity

@Dao
interface PaymentMethodDao {

    // Возвращает методы, у которых userId IS NULL (глобальные) или userId = :userId
    @Query("""
        SELECT * FROM payment_methods 
        WHERE userId IS NULL OR userId = :userId
        ORDER BY name ASC
    """)
    suspend fun getAllForUser(userId: String): List<PaymentMethodEntity>

    // Также оставим метод для получения всех (при админских задачах)
    @Query("SELECT * FROM payment_methods ORDER BY name ASC")
    suspend fun getAll(): List<PaymentMethodEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(method: PaymentMethodEntity)
}
