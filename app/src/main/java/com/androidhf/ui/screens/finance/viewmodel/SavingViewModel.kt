package com.androidhf.ui.screens.finance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidhf.data.datatypes.Savings
import com.androidhf.data.repository.SavingsRepository
import com.androidhf.data.enums.SavingsType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


//viewmodel létrehozása, ott ahol kell: val viewModel: SavingViewModel = hiltViewModel()
//biztosítja az adathozzáférést
@HiltViewModel
class SavingViewModel @Inject constructor(
    private val savingsRepository: SavingsRepository
): ViewModel() {

    private val _savings = MutableStateFlow<List<Savings>>(emptyList())
    /**
     * elérés UI-ból -> val savings by viewModel.savings.collectAsState()
     */
    val savings: StateFlow<List<Savings>> = _savings //UI látja, csak olvasható


    private val _messages = MutableSharedFlow<String>(replay = 0)
    val messages = _messages.asSharedFlow()

    init
    {
        loadSavings()
    }

    //betölti a tárolt adatokat
    private fun loadSavings()
    {
        viewModelScope.launch {
            savingsRepository.getAllSavings().collect { item: List<Savings> ->
                _savings.value = item
            }
        }
    }

    fun addSaving(save: Savings) {
        viewModelScope.launch {
            savingsRepository.addSaving(save)
        }
    }

    fun deleteSaving(save: Savings) {
        viewModelScope.launch {
            savingsRepository.deleteSaving(save)
        }
    }

    fun updateSaving(save: Savings) {
        viewModelScope.launch {
            savingsRepository.updateSaving(save)
        }
    }

    fun transactionAdded(osszeg: Int)
    {
        viewModelScope.launch {
            _savings.value.forEach { item ->
                if (osszeg < 0 && item.Type == SavingsType.EXPENSEGOAL_BYAMOUNT && item.Amount >= (item.Start + osszeg)) {
                    Log.d("transactionAdded", "Condition for failed met for saving ID: ${item.Id}. Emitting message.")
                    _messages.emit("${item.Title} has been failed!")
                } else if (item.Type == SavingsType.INCOMEGOAL_BYAMOUNT && item.Amount <= (item.Start + osszeg)) {
                    Log.d("transactionAdded", "Condition for completed met for saving ID: ${item.Id}. Emitting message.")
                    _messages.emit("${item.Title} has been completed!")
                } else {
                    Log.d("transactionAdded", "No condition met for saving ID: ${item.Id}")
                }
            }
        }
    }

    fun dateChecker(balance: Int)
    {
        viewModelScope.launch {
            _savings.value.forEach { item ->
                Log.d("dateChecker", "Belepés")
                if (!item.Closed)
                {
                    if (item.Type == SavingsType.INCOMEGOAL_BYTIME && item.EndDate < LocalDate.now() && item.Amount > balance) {
                        item.Failed = true
                        item.Closed = true
                        _messages.emit("${item.Title} has been failed!")
                        updateSaving(item)
                    }
                    else if (item.Type == SavingsType.INCOMEGOAL_BYTIME && item.EndDate < LocalDate.now() && item.Amount <= balance) {
                        item.Completed = true
                        item.Closed = true
                        _messages.emit("${item.Title} has been successfully completed!")
                        updateSaving(item)
                    }
                    else if (item.Type == SavingsType.INCOMEGOAL_BYAMOUNT && item.EndDate < LocalDate.now() && item.Amount > item.Start)
                    {
                        item.Failed = true
                        item.Closed = true
                        _messages.emit("${item.Title} has been failed!")
                        updateSaving(item)
                    }
                    else if (item.Type == SavingsType.EXPENSEGOAL_BYAMOUNT && item.EndDate < LocalDate.now() && item.Amount < item.Start)
                    {
                        item.Completed = true
                        item.Closed = true
                        _messages.emit("${item.Title} has been completed!")
                        Log.d("dateChecker", "${item.Id}, closed: ${item.Closed}")
                        updateSaving(item)
                    }
                }
            }
        }
    }

    fun savingsCount(): Int
    {
        return _savings.value.size
    }

    fun savingsCountByCollect(): Int
    {
        return _savings.value.filter { it.Type == SavingsType.INCOMEGOAL_BYAMOUNT }.size
    }

    fun savingsCountByLimit(): Int
    {
        return _savings.value.filter { it.Type == SavingsType.EXPENSEGOAL_BYAMOUNT }.size
    }

    fun savingsCountByHold(): Int
    {
        return _savings.value.filter { it.Type == SavingsType.INCOMEGOAL_BYTIME }.size
    }

    fun savingsCountCompleted(): Int
    {
        return _savings.value.filter { it.Completed }.size
    }

    fun savingsCountFailed(): Int
    {
        return _savings.value.filter { it.Failed }.size
    }
}