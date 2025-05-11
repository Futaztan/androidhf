package com.androidhf.ui.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidhf.data.Savings
import com.androidhf.data.SavingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
}