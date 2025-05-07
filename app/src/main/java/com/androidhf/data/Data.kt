package com.androidhf.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.Room
import com.androidhf.data.database.RoomDB
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

object Data {
    private var incomesList = mutableStateListOf<Transaction>()

    private var expensesList = mutableStateListOf<Transaction>()
    private var savingsList = mutableStateListOf<Savings>()

    var osszpenz by mutableIntStateOf(0)
    private set

    var topBarTitle by mutableStateOf("Home")
    var repetitiveTransactions = mutableStateListOf<Transaction>()

    private lateinit var roomDB: RoomDB



    fun init(context: Context) {
        roomDB = Room.databaseBuilder(
                context.applicationContext,
                RoomDB::class.java,
                "app_database"
            ).fallbackToDestructiveMigration(true)
            .build()

    }
    private suspend fun saveSaves(save : Savings) : Long
    {
         return roomDB.savingDao().insertSaving(save.toEntity())
    }
    suspend fun loadSaves()
    {
        val loaded = roomDB.savingDao().getAllSavings()
        val converted = loaded.map { it.toDomain() }
        savingsList.addAll(converted)
    }
    suspend fun deleteSave(save: Savings)
    {
        roomDB.savingDao().deleteSavingById(save.id)
        savingsList.remove(save)
    }
    private suspend fun saveTransaction(transaction: Transaction) : Long
    {
        return roomDB.transactionDao().insertTransaction(transaction.toEntity())
    }
    suspend fun loadTransactions()
    {

        var loaded = roomDB.transactionDao().getTransactionsByType("EXPENSE")
        var converted = loaded.map { it.toDomain() }
        expensesList.addAll(converted)

        loaded = roomDB.transactionDao().getTransactionsByType("INCOME")
        converted = loaded.map { it.toDomain() }
        incomesList.addAll(converted)

        loaded = roomDB.transactionDao().getRepetitiveTransactions(true)
        converted = loaded.map { it.toDomain() }
        repetitiveTransactions.addAll(converted)

        calculateOsszpenz()
    }

    fun getIncomesList() : SnapshotStateList<Transaction> {return incomesList}
    fun getExpensesList() : SnapshotStateList<Transaction> { return expensesList}
    fun getSavingsList() : SnapshotStateList<Savings> {return savingsList}

    fun dataToAIPrompt(): String {
        var output: String = "Ezek a bevételeim az elmúlt 30 napban (formátum: összeg;típus;időpont): "
        incomesList.forEach { item ->
            if (item.date.isAfter(LocalDate.now().minusDays(30))) {
                output += item.amount.toString() + ";" + item.category.toString() + ";" + item.date.toString() + " "
            }
        }
        output += "ezek a kiadásaim, ugyan az a formátum:"
        expensesList.forEach { item ->
            if (item.date.isAfter(LocalDate.now().minusDays(30))) {
                output += item.amount.toString() + ";" + item.category.toString() + ";" + item.date.toString() + " "
            }
        }

        //TODO: ide kell még a repetitiveTransactions

        //TODO: ez csak félkész, majd szét kell szednem típusok alapján
        output += "ezek a takarékaim, céljaim (formátum: megtakarítandó_összeg;kezdet;cél_vége;neve):"
        savingsList.forEach{ items ->
            output += items.Amount.toString() + ";" + items.StartDate.toString() + ";" + items.EndDate.toString() + ";" + items.Title + " "
        }
        //
        output += "ezek alapján milyen tanácsokat tudnál nekem adni pénzügyi szempontból?"
        return output
    }


    suspend fun addSave(save : Savings)
    {

        val id = saveSaves(save)
        val saveWithId = save.copy(id = id)
        savingsList.add(saveWithId)
    }

    //ehhez hozzaadtam a financeViewModellt, mert kell a saving kezeléshez
    suspend fun addTransaction(transaction: Transaction)
    {
        if(transaction.amount==0) throw IllegalArgumentException()
        val id =saveTransaction(transaction)
        val transactionWithId = transaction.copy(id = id)
        if(transaction.amount<0)
        {

            savingsList.forEach{ item ->
                if(item.Type == SavingsType.EXPENSEGOAL_BYAMOUNT)
                {
                    item.Start += transaction.amount
                }
            }
            expensesList.add(transactionWithId)
        }
        else
        {
            //hozzáadja a bevételt a megfelelő savings típusokhoz
            savingsList.forEach{ item ->
                if(item.Type == SavingsType.INCOMEGOAL_BYAMOUNT)
                {
                    item.Start += transaction.amount
                }
            }
            incomesList.add(transactionWithId)
        }
        calculateOsszpenz()

    }

    private fun calculateOsszpenz()
    {
        osszpenz = incomesList.sumOf { it.amount } + expensesList.sumOf { it.amount }
    }


    //ez mi gyuri
    //kell a finance graphhoz david
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
}
