package com.androidhf.data.modules

import android.content.Context
import androidx.room.Room
import com.androidhf.data.dao.RepetitiveTransactionDao
import com.androidhf.data.dao.SavingDao
import com.androidhf.data.dao.TransactionDao
import com.androidhf.data.database.RoomDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
//ameddig az alkalmazás él, ez is
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton //ebből egy egyet hoz létre, és utána meg azt használja
    fun provideDatabase(@ApplicationContext appContext: Context): RoomDB {
        return Room.databaseBuilder(
            appContext,
            RoomDB::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideRepetitiveTransactionDao(database: RoomDB): RepetitiveTransactionDao {
        return database.repTransactionDao()
    }

    @Provides
    fun provideSavingDao(database: RoomDB): SavingDao {
        return database.savingDao()
    }

    @Provides
    fun provideTransactionDao(database: RoomDB): TransactionDao {
        return database.transactionDao()
    }
}