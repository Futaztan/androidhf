package com.androidhf.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androidhf.data.datatypes.CompanyEntity
import com.androidhf.data.datatypes.StockEntity
import com.androidhf.data.datatypes.RepetitiveTransactionEntity
import com.androidhf.data.datatypes.SavingsEntity
import com.androidhf.data.datatypes.TransactionEntity
import com.androidhf.data.converters.Converters
import com.androidhf.data.dao.CompanyDao
import com.androidhf.data.dao.RepetitiveTransactionDao
import com.androidhf.data.dao.SavingDao
import com.androidhf.data.dao.StockDao
import com.androidhf.data.dao.TransactionDao

@Database(entities = [
    TransactionEntity::class,
    SavingsEntity::class,
    RepetitiveTransactionEntity::class,
    StockEntity::class,
    CompanyEntity::class],
    version = 6)

@TypeConverters(Converters::class)
abstract class RoomDB : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun savingDao(): SavingDao
    abstract fun repTransactionDao(): RepetitiveTransactionDao
    abstract fun stockDao(): StockDao
    abstract fun companyDao(): CompanyDao
}
