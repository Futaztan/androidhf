package com.androidhf.ui.screens.finance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidhf.data.datatypes.RepetitiveTransaction
import com.androidhf.data.repository.RepetitiveTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepetitiveTransactionViewModel @Inject constructor(
    private val repTransactionRepository: RepetitiveTransactionRepository
): ViewModel() {

    private var _repetitiveTransactionList = mutableListOf<List<RepetitiveTransaction>>(emptyList())
    var repetitiveTransactionList = _repetitiveTransactionList

    init {
        loadRepTransactions()
    }

    private fun loadRepTransactions() {
       viewModelScope.launch(Dispatchers.IO) {
           _repetitiveTransactionList = mutableListOf(repTransactionRepository.getAllRepetitiveTransactions())
       }
    }

    fun addRepTransaction(reptransaction: RepetitiveTransaction) {
        viewModelScope.launch {
            repTransactionRepository.addRepetitiveTransaction(reptransaction)
        }
    }

    //ez most komoly
     suspend fun fasz(): List<RepetitiveTransaction> {
        return repTransactionRepository.getAllRepetitiveTransactions()
    }


}