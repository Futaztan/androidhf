package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.time.LocalDateTime

//globális adatok az egész appban
/*!!LISTÁK ÉS TRANZAKCIOK KEZELÉSE:!!

incomesList: minden bevétel itt van, ha ismétlődő akkor is, a benne lévő amount mindig >0
expensesList: minden kiadás, ha ismétlődő akkor is, BENNE LÉVŐ AMOUNT MINDIG < 0 !!!!!!!!
osszpenz: minden kiadás és bevétel összeadva

Tranzaikciót csak az addTranstaction() al lehet hozzáadni, mert minden más privát. Ez eldönti h melyik listába való
ha kiadást akarunk hozzáadni akkor hozzá kell írni a paraméter Transaction amount-jába h negatív

Az osszpenzt mindent tranzakció után újra számolja magától és lehet gettelni simán

 */

object Data {
    private var incomesList = mutableStateListOf<Transaction>()

    private var expensesList = mutableStateListOf<Transaction>()

    var osszpenz by mutableIntStateOf(0)
    private set

    var topBarTitle by mutableStateOf("Home")
    var repetitiveTransactions = mutableStateListOf<Transaction>()

    //TODO: Ennek oda kell majd adni a savingViewModellt

    fun getIncomesList() : SnapshotStateList<Transaction> {return incomesList}
    fun getExpensesList() : SnapshotStateList<Transaction> { return expensesList}

    fun addTransaction(transaction: Transaction)
    {
        if(transaction.amount==0) throw IllegalArgumentException()
        if(transaction.amount<0)
        {
            expensesList.add(transaction)
        }
        else incomesList.add(transaction)
        calculateOsszpenz()

    }

    private fun calculateOsszpenz()
    {
        osszpenz = incomesList.sumOf { it.amount } + expensesList.sumOf { it.amount }
    }


    //ez mi gyuri
    fun calculateBalanceChangesSimple(): List<Int> {
        val allTransactions = mutableListOf<Pair<Transaction, Boolean>>()

        // Bevételek hozzáadása (true jelzi, hogy bevétel)
        incomesList.forEach { income ->
            allTransactions.add(Pair(income, true))
        }

        // Kiadások hozzáadása (false jelzi, hogy kiadás)
        expensesList.forEach { expense ->
            allTransactions.add(Pair(expense, false))
        }

        // Rendezés dátum és idő szerint
        allTransactions.sortBy { (transaction, _) ->
            LocalDateTime.of(transaction.date, transaction.time)
        }
        var currentBalance = 0
        val balanceChanges = mutableListOf<Pair<LocalDateTime, Int>>()

        val balanceValues = mutableListOf<Int>()
        // Egyenleg számítása időrendi sorrendben

        allTransactions.forEach { (transaction, isIncome) ->
            if (isIncome) {
                currentBalance += transaction.amount
            } else {
                currentBalance -= transaction.amount
            }
            balanceValues.add(currentBalance)
        }

        return balanceValues
    }

    /*
    fun calculateBalanceChanges(): List<Pair<LocalDateTime, Double>> {
    // Egyesítjük a bevételeket és kiadásokat egy listába
    val allTransactions = mutableListOf<Pair<Transaction, Boolean>>()

    // Bevételek hozzáadása (true jelzi, hogy bevétel)
    incomesList.forEach { income ->
        allTransactions.add(Pair(income, true))
    }

    // Kiadások hozzáadása (false jelzi, hogy kiadás)
    expensesList.forEach { expense ->
        allTransactions.add(Pair(expense, false))
    }

    // Rendezés dátum és idő szerint
    allTransactions.sortBy { (transaction, _) ->
        LocalDateTime.of(transaction.date, transaction.time)
    }

    // Egyenleg számítása időrendi sorrendben
    var currentBalance = 0.0
    val balanceChanges = mutableListOf<Pair<LocalDateTime, Double>>()

    allTransactions.forEach { (transaction, isIncome) ->
        // Ha bevétel, hozzáadjuk, ha kiadás, kivonjuk
        if (isIncome) {
            currentBalance += transaction.amount
        } else {
            currentBalance -= transaction.amount
        }

        // Az aktuális egyenleget és időpontot eltároljuk
        val dateTime = LocalDateTime.of(transaction.date, transaction.time)
        balanceChanges.add(Pair(dateTime, currentBalance))
    }

    return balanceChanges
}
     */
}
