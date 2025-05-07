package com.androidhf.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androidhf.data.RepetitiveTransactionEntity

@Dao
interface RepetitiveTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepTransaction(transaction: RepetitiveTransactionEntity) : Long

    @Query("SELECT * FROM repetitivetransactions WHERE type = :type")
    suspend fun getRepTransactionsByType(type: String): List<RepetitiveTransactionEntity>


    @Query("SELECT * FROM repetitivetransactions")
    suspend fun getAllRepTransactions(): List<RepetitiveTransactionEntity>
}