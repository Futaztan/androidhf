package com.androidhf.ui.screens.finance

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.androidhf.data.Savings

class SavingsViewModel : ViewModel() {
    // Állapotot itt tároljuk
    private val _savingsList = mutableStateListOf<Savings>()
    val savingsList: List<Savings> get() = _savingsList

    // Elem hozzáadása
    fun addSaving(savings: Savings) {
        _savingsList.add(savings)
    }

    // Elem törlése
    fun removeSaving(saving: Savings) {
        _savingsList.remove(saving)
    }
}