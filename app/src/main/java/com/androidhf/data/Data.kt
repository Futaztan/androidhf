package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.androidhf.ui.screens.finance.SavingsViewModel
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

    fun getIncomesList() : SnapshotStateList<Transaction> {return incomesList}
    fun getExpensesList() : SnapshotStateList<Transaction> { return expensesList}


    //ehhez hozzaadtam a financeViewModellt, mert kell a saving kezeléshez
    fun addTransaction(transaction: Transaction/*, viewModel: SavingsViewModel*/)
    {
        if(transaction.amount==0) throw IllegalArgumentException()
        if(transaction.amount<0)
        {
            //kivonja a megfelelő savinglist elemekből
            /* TODO: dávid ezt meg kell beszélnünk, mert ez kell ide nekem de akkor nem lesz kompatibilis a workerrel
            viewModel.savingsList.forEach{ item ->
                if(item.Type == SavingsType.EXPENSEGOAL_BYAMOUNT)
                {
                    item.Amount -= transaction.amount
                }
            }*/
            expensesList.add(transaction)
        }
        else
        {
            //hozzáadja a bevételt a megfelelő savings típusokhoz
            /*viewModel.savingsList.forEach{ item ->
                if(item.Type == SavingsType.EXPENSEGOAL_BYAMOUNT)
                {
                    item.Amount -= transaction.amount
                }
            }*/
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
