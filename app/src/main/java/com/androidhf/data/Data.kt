package com.androidhf.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.room.Room
import com.androidhf.data.database.RoomDB
import java.time.LocalDate

import java.time.LocalDateTime
import kotlin.math.exp


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
    var savingsList = mutableStateListOf<Savings>()

    var osszpenz by mutableIntStateOf(0)
    private set

    var topBarTitle by mutableStateOf("Home")
    var repetitiveTransactions = mutableStateListOf<Transaction>()

    lateinit var db: RoomDB

    fun init(context: Context) {
        db = Room.databaseBuilder(
                context.applicationContext,
                RoomDB::class.java,
                "app_database"
            ).fallbackToDestructiveMigration(true)
            .build()
    }
    suspend fun saveTransactions()
    {
        incomesList.forEach {
            db.transactionDao().insertTransaction(it.toEntity())
        }
        expensesList.forEach {
            db.transactionDao().insertTransaction(it.toEntity())
        }
        TODO()  //TODO ISMETLODO TRANZAKCIOK
    }
    suspend fun loadTransactions()
    {

        var loaded = db.transactionDao().getTransactionsByType("EXPENSE")
        var converted = loaded.map { it.toDomain() }
        expensesList.addAll(converted)

        loaded = db.transactionDao().getTransactionsByType("INCOME")
        converted = loaded.map { it.toDomain() }
        incomesList.addAll(converted)


    }

    fun getIncomesList() : SnapshotStateList<Transaction> {return incomesList}
    fun getExpensesList() : SnapshotStateList<Transaction> { return expensesList}

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


    //ehhez hozzaadtam a financeViewModellt, mert kell a saving kezeléshez
    fun addTransaction(transaction: Transaction)
    {
        if(transaction.amount==0) throw IllegalArgumentException()
        if(transaction.amount<0)
        {

            savingsList.forEach{ item ->
                if(item.Type == SavingsType.EXPENSEGOAL_BYAMOUNT)
                {
                    item.Start += transaction.amount
                }
            }
            expensesList.add(transaction)
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
            incomesList.add(transaction)
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
