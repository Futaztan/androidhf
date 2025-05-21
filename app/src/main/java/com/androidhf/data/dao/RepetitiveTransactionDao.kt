package com.androidhf.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androidhf.data.datatypes.RepetitiveTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepetitiveTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepTransaction(transaction: RepetitiveTransactionEntity) : Long

    @Query("SELECT * FROM repetitivetransactions WHERE type = :type")
    fun getRepTransactionsByType(type: String): Flow<List<RepetitiveTransactionEntity>>


    @Query("SELECT * FROM repetitivetransactions")
    fun getAllRepTransactions(): Flow<List<RepetitiveTransactionEntity>>

    @Query("DELETE FROM repetitivetransactions WHERE id = :id")
    suspend fun deleteRepTransactionById(id: Long)

    @Query("DELETE FROM repetitivetransactions")
    suspend fun clearTable()

}