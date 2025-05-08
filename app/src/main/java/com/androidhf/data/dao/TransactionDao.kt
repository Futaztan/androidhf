package com.androidhf.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androidhf.data.TransactionEntity


@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity) : Long

    @Query("SELECT * FROM transactions WHERE type = :type")
    suspend fun getTransactionsByType(type: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<TransactionEntity>
}