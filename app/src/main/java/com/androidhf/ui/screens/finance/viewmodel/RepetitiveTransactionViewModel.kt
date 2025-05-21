package com.androidhf.ui.screens.finance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidhf.data.datatypes.RepetitiveTransaction
import com.androidhf.data.datatypes.Transaction
import com.androidhf.data.repository.RepetitiveTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepetitiveTransactionViewModel @Inject constructor(
    private val repTransactionRepository: RepetitiveTransactionRepository
): ViewModel() {



    private val _repetitiveTransactions = MutableStateFlow<List<RepetitiveTransaction>>(emptyList())
    val repetitiveTransactions: StateFlow<List<RepetitiveTransaction>> = _repetitiveTransactions.asStateFlow()


    init {
        loadRepTransactions()
        Log.e("tag-init","rep")
    }


     fun loadRepTransactions() {
       viewModelScope.launch(Dispatchers.IO) {
           repTransactionRepository.getAllRepetitiveTransactions().collect {reptransactionList ->
               _repetitiveTransactions.value = reptransactionList
           }

       }

    }
    fun deleteAll()
    {
        viewModelScope.launch {
            repTransactionRepository.deleteAll()
        }
    }

    fun addRepTransaction(reptransaction: RepetitiveTransaction) {
        viewModelScope.launch {
            repTransactionRepository.addRepetitiveTransaction(reptransaction)
        }
    }



}