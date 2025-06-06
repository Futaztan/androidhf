package com.androidhf.data.repository

import android.util.Log
import com.androidhf.data.datatypes.Savings
import com.androidhf.data.enums.SavingsType
import com.androidhf.data.datatypes.Transaction
import com.androidhf.data.dao.TransactionDao
import com.androidhf.data.database.FirebaseDB
import com.androidhf.ui.screens.login.auth.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton //csak egy darab jön létre belőle
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val savingsRepository: SavingsRepository,
    private val firebaseDB: FirebaseDB
) {

    suspend fun getAllTransactions(): Flow<List<Transaction>> {

        if(AuthService.isLoggedIn())
        {

            val firestorelist = firebaseDB.getAllTransactionsFromFirebase()
            for(i in firestorelist.indices)
            {
                transactionDao.insertTransaction(firestorelist.get(i).toEntity())
            }

        }
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getIncomeTransactions(): Flow<List<Transaction>> {

        if(AuthService.isLoggedIn())
        {

            val firestorelist = firebaseDB.getIncomeTransactionFromFirebase()
            for(i in firestorelist.indices)
            {
                transactionDao.insertTransaction(firestorelist.get(i).toEntity())
            }

        }
        return transactionDao.getTransactionsByType("INCOME").map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getExpenseTransactions(): Flow<List<Transaction>> {
        if(AuthService.isLoggedIn())
        {

            val firestorelist = firebaseDB.getExpenseTransactionFromFirebase()
            for(i in firestorelist.indices)
            {
                transactionDao.insertTransaction(firestorelist.get(i).toEntity())
            }

        }
        return transactionDao.getTransactionsByType("EXPENSE").map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addTransaction(transaction: Transaction) {
        if (transaction.amount == 0) throw IllegalArgumentException("Amount can't be zero")

        val id = transactionDao.insertTransaction(transaction.toEntity())
        val transactionWithId = transaction.copy(id = id)

        if (AuthService.isLoggedIn()) {
            firebaseDB.addTransactionToFirebase(transactionWithId)
        }

        val currentSavingsList = savingsRepository.getAllSavings().first()

        val savingsToUpdate = mutableListOf<Savings>()

        currentSavingsList.forEach { saving ->
            val updatedSaving = saving.copy()

            if(transaction.amount < 0 && updatedSaving.Type == SavingsType.EXPENSEGOAL_BYAMOUNT)
            {
                Log.d("TransactionRepository", "Transaction amount: ${transaction.amount}")
                updatedSaving.Start += transaction.amount
                if(!updatedSaving.Closed && updatedSaving.Amount >= updatedSaving.Start)
                {
                    updatedSaving.Failed = true
                    updatedSaving.Closed = true
                }
                savingsToUpdate.add(updatedSaving)
            }
            else if(transaction.amount > 0 && updatedSaving.Type == SavingsType.INCOMEGOAL_BYAMOUNT)
            {
                Log.d("TransactionRepository", "Transaction amount: ${transaction.amount}")
                updatedSaving.Start += transaction.amount
                if(!updatedSaving.Closed && updatedSaving.Amount <= updatedSaving.Start)
                {
                    updatedSaving.Completed = true
                    updatedSaving.Closed = true
                }
                savingsToUpdate.add(updatedSaving)
            }
        }
        savingsToUpdate.forEach { updatedSaving ->
            savingsRepository.updateSaving(updatedSaving)
        }
    }

    suspend fun deleteAll()
    {
        transactionDao.clearTable()
    }
    suspend fun deleteTransaction(transaction: Transaction)
    {
        if(AuthService.isLoggedIn())
        {
            firebaseDB.deleteTransactionFromFirebase(transaction)
        }
        transactionDao.deleteTransactionById(transaction.id)
    }

    /*
    fun calculateBalanceChangesSimple(): List<Int> {
        val allTransactions = mutableListOf<Pair<Transaction, Boolean>>()

        incomesList.forEach { income ->
            allTransactions.add(Pair(income, true))
        }

        expensesList.forEach { expense ->
            allTransactions.add(Pair(expense, false))
        }

        allTransactions.sortBy { (transaction, _) ->
            LocalDateTime.of(transaction.date, transaction.time)
        }
        var currentBalance = 0
        val balanceChanges = mutableListOf<Pair<LocalDateTime, Int>>()

        val balanceValues = mutableListOf<Int>()

        allTransactions.forEach { (transaction, isIncome) ->
            if (isIncome) {
                currentBalance += transaction.amount
            } else {
                currentBalance = transaction.amount
            }
            balanceValues.add(currentBalance)
        }

        return balanceValues
    }
     */
}