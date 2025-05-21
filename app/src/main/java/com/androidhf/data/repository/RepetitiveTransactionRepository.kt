package com.androidhf.data.repository

import com.androidhf.data.datatypes.RepetitiveTransaction
import com.androidhf.data.datatypes.Savings
import com.androidhf.data.enums.SavingsType
import com.androidhf.data.dao.RepetitiveTransactionDao
import com.androidhf.data.database.FirebaseDB
import com.androidhf.ui.screens.login.auth.AuthService
import com.google.rpc.context.AttributeContext.Auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepetitiveTransactionRepository @Inject constructor(
    private val repTransactionDao: RepetitiveTransactionDao,
    private val savingsRepository: SavingsRepository,
    private val firebaseDB: FirebaseDB
) {

    suspend fun getAllRepetitiveTransactions(): List<RepetitiveTransaction> {
        if(AuthService.isLoggedIn())
        {
            return firebaseDB.getRepTransactionsFromFirebase()
        }
        return repTransactionDao.getAllRepTransactions().map { it.toDomain() }
    }

    suspend fun deleteRepetitiveTransaction(repTransaction: RepetitiveTransaction)
    {
        //TODO
        repTransactionDao.deleteRepTransactionById(repTransaction.transaction.id)
    }

    fun getRepetitiveTransactionsByType(type: String): Flow<List<RepetitiveTransaction>> {
        return repTransactionDao.getRepTransactionsByType(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addRepetitiveTransaction(repTransaction: RepetitiveTransaction) {
        if (repTransaction.transaction.amount == 0) throw IllegalArgumentException("Amount can't be zero")

        val id = repTransactionDao.insertRepTransaction(repTransaction.toEntity())
        val withId = repTransaction.copy(transaction = repTransaction.transaction.copy(id = id))

        if (AuthService.isLoggedIn())
        {
            firebaseDB.addRepetitiveTransactionToFirebase(withId)
        }

        val currentSavingsList = savingsRepository.getAllSavings().first()

        val savingsToUpdate = mutableListOf<Savings>()

        currentSavingsList.forEach { saving ->
            val updatedSaving = saving.copy()

            if(repTransaction.transaction.amount < 0 && updatedSaving.Type == SavingsType.EXPENSEGOAL_BYAMOUNT)
            {
                updatedSaving.Start += repTransaction.transaction.amount
                savingsToUpdate.add(updatedSaving)
            }
            else if(repTransaction.transaction.amount > 0 && updatedSaving.Type == SavingsType.INCOMEGOAL_BYAMOUNT)
            {
                updatedSaving.Start += repTransaction.transaction.amount
                savingsToUpdate.add(updatedSaving)
            }
        }
        savingsToUpdate.forEach { updatedSaving ->
            savingsRepository.updateSaving(updatedSaving)
        }
    }
}