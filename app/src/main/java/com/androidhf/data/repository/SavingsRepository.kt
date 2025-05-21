package com.androidhf.data.repository

import android.util.Log
import com.androidhf.data.datatypes.Savings
import com.androidhf.data.dao.SavingDao
import com.androidhf.data.database.FirebaseDB
import com.androidhf.ui.screens.login.auth.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

//csak egy legyen belőle az alkalmazás folyamán (egyszer létrehozza tutána meg ezt használja)
@Singleton
class SavingsRepository @Inject constructor(
    private val savingDao: SavingDao,
    private val firebaseDB: FirebaseDB
) {
    suspend fun getAllSavings(): Flow<List<Savings>>
    {

        if(AuthService.isLoggedIn())
        {
            return flowOf(firebaseDB.getSavingsFromFirebase())
        }
        return savingDao.getAllSavings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addSaving(save: Savings) {
        val id = savingDao.insertSaving(save.toEntity())
        val withId = save.copy(Id = id)
        if (AuthService.isLoggedIn())
        {
            firebaseDB.addSavingToFirebase(withId)
        }
    }

    suspend fun deleteSaving(save: Savings) {
        savingDao.deleteSavingById(save.Id)
    }

    suspend fun updateSaving(save: Savings) {
        Log.d("SavingsRepository", "Attempting to update saving with ID: ${save.Id}, amount: ${save.Start}")
        Log.d("SavingsRepository", "ID being passed to DAO: ${save.toEntity().id}") // Extra ellenőrzés
        try {
            val rowsAffected = savingDao.updateSaving(save.toEntity())
            if (rowsAffected > 0) {
                Log.d("SavingsRepository", "Saving updated successfully. Rows affected: $rowsAffected")
                if (AuthService.isLoggedIn()) {
                    firebaseDB.updateSavingInFirebase(save)
                }
            } else {
                Log.e("SavingsRepository", "Saving update did not affect any rows! Saving ID: ${save.Id}")
            }
        } catch (e: Exception) {
            Log.e("SavingsRepository", "Error updating saving with ID: ${save.Id}", e)
        }
    }
}