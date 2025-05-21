package com.androidhf.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androidhf.data.datatypes.TransactionEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity) : Long

    //ezenek sem kell suspend mert Flow alapból háttérszálon megy
    @Query("SELECT * FROM transactions WHERE type = :type")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): Flow<List<TransactionEntity>>


    @Query("DELETE FROM transactions")
    suspend fun clearTable()
}