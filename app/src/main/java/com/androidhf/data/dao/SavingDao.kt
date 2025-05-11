package com.androidhf.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.androidhf.data.SavingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaving(saving: SavingsEntity) : Long

    //nem kell suspend mert Flow alapból háttérszálon megy
    @Query("SELECT * FROM savings")
    fun getAllSavings(): Flow<List<SavingsEntity>>

    @Query("DELETE FROM savings WHERE id = :id")
    suspend fun deleteSavingById(id: Long)

    @Update
    suspend fun updateSaving(saving: SavingsEntity): Int
}