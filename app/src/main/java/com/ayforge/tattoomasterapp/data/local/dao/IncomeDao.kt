package com.ayforge.tattoomasterapp.data.local.dao

import androidx.room.*
import com.ayforge.tattoomasterapp.data.local.entity.IncomeEntity
import java.time.LocalDateTime

@Dao
interface IncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: IncomeEntity)

    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getAllByUser(userId: String): List<IncomeEntity>

    // Выборка доходов за день по диапазону времени (00:00–23:59)
    @Query(
        """
        SELECT * FROM incomes 
        WHERE userId = :userId 
          AND createdAt >= :startOfDay 
          AND createdAt < :endOfDay
        ORDER BY createdAt DESC
        """
    )
    suspend fun getByDate(
        userId: String,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): List<IncomeEntity>

    @Query("DELETE FROM incomes WHERE id = :id AND userId = :userId")
    suspend fun deleteById(id: Long, userId: String)
}
