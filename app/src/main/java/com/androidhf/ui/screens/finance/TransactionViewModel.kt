package com.androidhf.ui.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidhf.data.Category
import com.androidhf.data.Transaction
import com.androidhf.data.TransactionRepository
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
    }

    fun get30DaysIncome(): Int {
        val date = LocalDate.now().minusDays(30)
        return _incomeTransactions.value.filter { it.date >= date }.sumOf { it.amount }
    }

    fun get30DaysExpense(): Int {
        val date = LocalDate.now().minusDays(30)
        return _expenseTransactions.value.filter { it.date >= date }.sumOf { it.amount }
    }

    fun get30DaysIncomeByType(): String {
        val date = LocalDate.now().minusDays(30)
        val list = _incomeTransactions.value.filter { it.date >= date }
        return list
            .groupBy { it.category.displayName }
            .mapValues { item -> item.value.sumOf { it.amount } }
            .maxByOrNull { it.value }?.key ?: ""
    }

    fun get30DaysExpenseByType(): String {
        val date = LocalDate.now().minusDays(30)
        val list = _expenseTransactions.value.filter { it.date >= date }
        return list
            .groupBy { it.category.displayName }
            .mapValues { item -> item.value.sumOf { it.amount } }
            .minByOrNull { it.value }?.key ?: ""
    }

    private fun loadTransactions() {
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