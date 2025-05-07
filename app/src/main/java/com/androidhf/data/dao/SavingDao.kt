package com.androidhf.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androidhf.data.SavingsEntity

@Dao
interface SavingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaving(saving: SavingsEntity) : Long

    @Query("SELECT * FROM savings")
    suspend fun getAllSavings(): List<SavingsEntity>

    @Query("DELETE FROM savings WHERE id = :id")
    suspend fun deleteSavingById(id: Long)
}