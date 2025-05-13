package com.androidhf.data.modules

import com.androidhf.data.database.FirebaseDB
import dagger.hilt.components.SingletonComponent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)//az egész alkalmazás életciklusa alatt aktív
object FirebaseModule {

    @Provides
    @Singleton //csak egy darab lesz belőle, mindenk ezt a példányt kapja
    fun provideFirebaseDB(): FirebaseDB {
        // Itt hozod létre a FirebaseDB példányát
        return FirebaseDB()
    }
}