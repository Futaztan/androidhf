package com.androidhf.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androidhf.data.TransactionEntity
import com.androidhf.data.converters.Converters
import com.androidhf.data.dao.TransactionDao

@Database(entities = [TransactionEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class RoomDB : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

}
