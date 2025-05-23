package com.androidhf.ui.screens.finance.viewmodel

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidhf.data.datatypes.Transaction
import com.androidhf.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


//viewmodel létrehozása, ott ahol kell: val viewModel: TransactionViewModel = hiltViewModel()
//biztosítja az adathozzáférést
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
): ViewModel() {

    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    /**
     * elérés UI-ból -> val transactions by viewModel.transactions.collectAsState()
     */
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions.asStateFlow() //UI látja, csak olvasható

    private val _incomeTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val incomeTransactions: StateFlow<List<Transaction>> = _incomeTransactions.asStateFlow()

    private val _expenseTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val expenseTransactions: StateFlow<List<Transaction>> = _expenseTransactions.asStateFlow()

    /**
     * használat: val money = viewmodel.balance.collectAsState().value
     */
    val balance: StateFlow<Int> = combine(
        _incomeTransactions,
        _expenseTransactions
    ) { incomeList, expenseList ->
        incomeList.sumOf { it.amount } + expenseList.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        loadTransactions()

        Log.e("tag-init","transaction")
    }

    fun get30DaysIncome(): Int {
        val date = LocalDate.now().minusDays(30)
        return _incomeTransactions.value.filter { it.date >= date }.sumOf { it.amount }
    }

    fun get30DaysExpense(): Int {
        val date = LocalDate.now().minusDays(30)
        return _expenseTransactions.value.filter { it.date >= date }.sumOf { it.amount }
    }

    fun get30DaysIncomeByType(context: Context): String {
        val date = LocalDate.now().minusDays(30)
        val list = _incomeTransactions.value.filter { it.date >= date }
        return list
            .groupBy { it.category.getDisplayName(context) }
            .mapValues { item -> item.value.sumOf { it.amount } }
            .maxByOrNull { it.value }?.key ?: ""
    }

    fun get30DaysExpenseByType(context: Context): String {
        val date = LocalDate.now().minusDays(30)
        val list = _expenseTransactions.value.filter { it.date >= date }
        return list
            .groupBy { it.category.getDisplayName(context) }
            .mapValues { item -> item.value.sumOf { it.amount } }
            .minByOrNull { it.value }?.key ?: ""
    }

    fun sortIncomeTransactionsByAmount(asc: Boolean = false): List<Transaction> {
        return if (asc) {
            _incomeTransactions.value.sortedBy { it.amount }
        }
        else return _incomeTransactions.value.sortedByDescending { it.amount }
    }

    fun sortTransactionsByAmount(asc: Boolean = false, list: List<Transaction>): List<Transaction> {
        return if (asc) {
            list.sortedBy { it.amount }
        }
        else return list.sortedByDescending { it.amount }
    }

    fun sortExpenseTransactionsByAmount(asc: Boolean = false): List<Transaction> {
        return if (asc) {
            _expenseTransactions.value.sortedBy { it.amount }
        }
        else return _expenseTransactions.value.sortedByDescending { it.amount }
    }

    fun sortIncomeTransactionsByDate(asc: Boolean = false): List<Transaction> {
        return if (asc) {
            _incomeTransactions.value.sortedWith(
                compareBy<Transaction> { it.date }
                    .thenBy { it.time }
            )
        }
        else return _incomeTransactions.value.sortedWith(
            compareByDescending<Transaction> { it.date }
                .thenByDescending { it.time }
        )
    }

    fun sortTransactionsByDate(asc: Boolean = false, list: List<Transaction>): List<Transaction> {
        return if (asc) {
            list.sortedWith(
                compareBy<Transaction> { it.date }
                    .thenBy { it.time }
            )
        }
        else return list.sortedWith(
            compareByDescending<Transaction> { it.date }
                .thenByDescending { it.time }
        )
    }

    fun sortExpenseTransactionsByDate(asc: Boolean = false): List<Transaction> {
        return if (asc) {
            _expenseTransactions.value.sortedWith(
                compareBy<Transaction> { it.date }
                    .thenBy { it.time }
            )
        }
        else return _expenseTransactions.value.sortedWith(
            compareByDescending<Transaction> { it.date }
                .thenByDescending { it.time }
        )
    }

    fun sortIncomeByCategory(context: Context): List<Transaction> {
        return _incomeTransactions.value.sortedBy { it.category.getDisplayName(context) }
    }

    fun sortByCategory(list: List<Transaction>, context: Context): List<Transaction> {
        return list.sortedBy { it.category.getDisplayName(context) }
    }

    fun sortExpenseByCategory(context: Context): List<Transaction> {
        return _expenseTransactions.value.sortedBy { it.category.getDisplayName(context) }
    }

    fun loadTransactions() {

        viewModelScope.launch {
            transactionRepository.getAllTransactions().collect { transactionList ->
                _allTransactions.value = transactionList
            }
        }
        viewModelScope.launch {
            transactionRepository.getIncomeTransactions().collect { incomeList ->
                _incomeTransactions.value = incomeList
            }
        }
        viewModelScope.launch {
            transactionRepository.getExpenseTransactions().collect { expenseList ->
                _expenseTransactions.value = expenseList
            }
        }
    }
    fun deleteAll()
    {
        viewModelScope.launch {
            transactionRepository.deleteAll()
        }
    }

    fun expenseContainsList(value: String, context: Context): List<Transaction>
    {
        val string = value.lowercase()
        return _expenseTransactions.value.filter {
            it.amount.toString().lowercase().contains(string) ||
            it.category.getDisplayName(context).lowercase().contains(string) ||
            it.date.toString().lowercase().contains(string) ||
            it.time.toString().lowercase().contains(string) ||
            it.description.lowercase().contains(string)
        }
    }

    fun incomeContainsList(value: String, context: Context): List<Transaction>
    {
        val string = value.lowercase()
        return _incomeTransactions.value.filter {
            it.amount.toString().lowercase().contains(string) ||
            it.category.getDisplayName(context).lowercase().contains(string) ||
            it.date.toString().lowercase().contains(string) ||
            it.time.toString().lowercase().contains(string) ||
            it.description.lowercase().contains(string)
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.addTransaction(transaction)
        }
    }

    fun getSortedMoney(): List<Int> {
        return _allTransactions.value
            .sortedWith(compareBy<Transaction> { it.date }
                .thenBy { it.time })
            .map { it.amount }
    }
}