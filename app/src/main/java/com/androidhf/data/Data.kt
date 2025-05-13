package com.androidhf.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.Room
import com.androidhf.data.database.FirebaseDB
import com.androidhf.data.database.RoomDB
import com.androidhf.ui.screens.login.auth.AuthService
import java.time.LocalDate
import java.time.LocalDateTime


//globális adatok az egész appban
/*!!LISTÁK ÉS TRANZAKCIOK KEZELÉSE:!!

incomesList: minden bevétel itt van, ha ismétlődő akkor is, a benne lévő amount mindig >0
expensesList: minden kiadás, ha ismétlődő akkor is, BENNE LÉVŐ AMOUNT MINDIG < 0 !!!!!!!!
osszpenz: minden kiadás és bevétel összeadva
repetitiveTransactions: az összes ismétlődő tranzakció, ezeket ellenorzi az app

Tranzaikciót csak az addTranstaction() al lehet hozzáadni, mert minden más privát. Ez eldönti h melyik listába való
ha kiadást akarunk hozzáadni akkor hozzá kell írni a paraméter Transaction amount-jába h negatív

Az osszpenzt mindent tranzakció után újra számolja magától és lehet gettelni simán

 */
/*
object Data {

    private var incomesList = mutableStateListOf<Transaction>()

    private var expensesList = mutableStateListOf<Transaction>()
    private var savingsList = mutableStateListOf<Savings>()

    var osszpenz by mutableIntStateOf(0)
        private set

    var repetitiveTransactions = mutableStateListOf<RepetitiveTransaction>()

    private lateinit var roomDB: RoomDB
    private val firebaseDB = FirebaseDB()


    fun init(context: Context) {
        roomDB = Room.databaseBuilder(
            context.applicationContext,
            RoomDB::class.java,
            "app_database"
        ).fallbackToDestructiveMigration(true)
            .build()

    }

    private suspend fun saveSaving(save: Savings): Long {
        return roomDB.savingDao().insertSaving(save.toEntity())
    }

    suspend fun loadSavings() {
        val loaded = roomDB.savingDao().getAllSavings()
        val converted = loaded.map { it.toDomain() }
        savingsList.addAll(converted)
    }

    suspend fun deleteSaving(save: Savings) {
        roomDB.savingDao().deleteSavingById(save.id)
        savingsList.remove(save)
    }

    private suspend fun saveTransaction(transaction: Transaction): Long {
        return roomDB.transactionDao().insertTransaction(transaction.toEntity())
    }
    private suspend fun saveRepetitiveTransaction(repTransaction: RepetitiveTransaction) : Long
    {
        return roomDB.repTransactionDao().insertRepTransaction(repTransaction.toEntity())
    }

    suspend fun loadEveryTransactions() {


        var loaded = roomDB.transactionDao().getTransactionsByType("EXPENSE")
        var converted = loaded.map { it.toDomain() }
        expensesList.addAll(converted)

        loaded = roomDB.transactionDao().getTransactionsByType("INCOME")
        converted = loaded.map { it.toDomain() }
        incomesList.addAll(converted)

        val reploaded = roomDB.repTransactionDao().getAllRepTransactions()
        val repconverted = reploaded.map { it.toDomain() }
        repetitiveTransactions.addAll(repconverted)

        calculateOsszpenz()
    }

    fun getIncomesList(): SnapshotStateList<Transaction> {
        return incomesList
    }

    fun getExpensesList(): SnapshotStateList<Transaction> {
        return expensesList
    }

    fun getSavingsList(): SnapshotStateList<Savings> {
        return savingsList
    }

    suspend fun addSave(save: Savings) {

        val id = saveSaving(save)
        val saveWithId = save.copy(id = id)
        firebaseDB.addSavingToFirebase(saveWithId)
        savingsList.add(saveWithId)
    }

    suspend fun addRepetitiveTransaction(repTransaction: RepetitiveTransaction)
    {
        if (repTransaction.transaction.amount == 0) throw IllegalArgumentException()
        val id = saveRepetitiveTransaction(repTransaction)
        val transactionWithId = repTransaction.transaction.copy(id=id)
        val repTransactionWithId = repTransaction.copy(transaction = transactionWithId)
        repetitiveTransactions.add(repTransactionWithId)
        if(AuthService.isLoggedIn())
            firebaseDB.addRepetitiveTransactionToFireabase(repTransactionWithId)
    }

    suspend fun addTransaction(transaction: Transaction) {
        if (transaction.amount == 0) throw IllegalArgumentException()

        val id = saveTransaction(transaction)
        val transactionWithId = transaction.copy(id = id)

        if (AuthService.isLoggedIn())
            firebaseDB.addTransactionToFirebase(transactionWithId)

        if (transaction.amount < 0) {

            savingsList.forEach { item ->
                if (item.Type == SavingsType.EXPENSEGOAL_BYAMOUNT) {
                    item.Start += transaction.amount
                }
            }
            expensesList.add(transactionWithId)
        } else {
            //hozzáadja a bevételt a megfelelő savings típusokhoz
            savingsList.forEach { item ->
                if (item.Type == SavingsType.INCOMEGOAL_BYAMOUNT) {
                    item.Start += transaction.amount
                }
            }
            incomesList.add(transactionWithId)
        }
        calculateOsszpenz()

    }

    private fun calculateOsszpenz() {
        osszpenz = incomesList.sumOf { it.amount } + expensesList.sumOf { it.amount }
    }
}
*/