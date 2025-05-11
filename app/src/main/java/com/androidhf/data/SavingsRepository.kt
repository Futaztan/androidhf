package com.androidhf.data

import com.androidhf.data.dao.SavingDao
import com.androidhf.data.database.FirebaseDB
import com.androidhf.ui.screens.login.auth.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

//csak egy legyen belőle az alkalmazás folyamán (egyszer létrehozza tutána meg ezt használja)
@Singleton
class SavingsRepository @Inject constructor(
    private val savingDao: SavingDao,
    private val firebaseDB: FirebaseDB
) {
    fun getAllSavings(): Flow<List<Savings>>
    {
        return savingDao.getAllSavings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addSaving(save: Savings) {
        val id = savingDao.insertSaving(save.toEntity())
        val withId = save.copy(id = id)
        if (AuthService.isLoggedIn())
        {
            firebaseDB.addSavingToFirebase(withId)
        }
    }

    suspend fun deleteSaving(save: Savings) {
        savingDao.deleteSavingById(save.id)
    }

    suspend fun updateSaving(save: Savings) {
        savingDao.updateSaving(save.toEntity())
        if (AuthService.isLoggedIn())
        {
            firebaseDB.updateSavingInFirebase(save)
        }
    }
}