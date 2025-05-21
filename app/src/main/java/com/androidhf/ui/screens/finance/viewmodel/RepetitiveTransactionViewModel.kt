package com.androidhf.ui.screens.finance.viewmodel

import android.util.Log
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

    private var repetitiveTransactionList = mutableListOf<List<RepetitiveTransaction>>(emptyList())


    init {
        loadRepTransactions()
        Log.e("tag-init","rep")
    }

    private fun loadRepTransactions() {
       viewModelScope.launch(Dispatchers.IO) {
           repetitiveTransactionList = mutableListOf(repTransactionRepository.getAllRepetitiveTransactions())
       }
    }


    fun addRepTransaction(reptransaction: RepetitiveTransaction) {
        viewModelScope.launch {
            repTransactionRepository.addRepetitiveTransaction(reptransaction)
        }
    }




}