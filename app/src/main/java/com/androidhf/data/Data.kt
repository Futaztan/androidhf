package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import java.time.LocalDateTime

object Data {
    var incomesList = mutableStateListOf<Transaction>()
    var expensesList = mutableStateListOf<Transaction>()
    var savingsList = mutableStateListOf<Savings>()
    var osszpenz by mutableIntStateOf(0)

    fun addOsszpenz(amount : Int)
    {
        osszpenz+=amount
    }

    fun Osszpenz()
    {
        osszpenz = incomesList.sumOf { it.amount } - expensesList.sumOf { it.amount }
    }

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
