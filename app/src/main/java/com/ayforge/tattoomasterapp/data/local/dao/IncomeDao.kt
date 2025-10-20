package com.ayforge.tattoomasterapp.data.local.dao

import androidx.room.*
import com.ayforge.tattoomasterapp.data.local.entity.IncomeEntity
import com.ayforge.tattoomasterapp.data.local.entity.IncomeWithClient
import java.time.LocalDateTime

@Dao
interface IncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: IncomeEntity)

    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getAllByUser(userId: String): List<IncomeWithClient>

    @Query("""
        SELECT * FROM incomes 
        WHERE userId = :userId 
        AND createdAt BETWEEN :start AND :end
        ORDER BY createdAt DESC
    """)
    suspend fun getByPeriod(
        userId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<IncomeWithClient>

    @Query("DELETE FROM incomes WHERE id = :id AND userId = :userId")
    suspend fun deleteById(id: Long, userId: String)
}
