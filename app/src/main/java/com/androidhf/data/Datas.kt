package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

object Datas {
    var osszpenz by mutableStateOf(0.0)
    var incomesList = mutableStateListOf<Double>()
    var expensesList = mutableStateListOf<Double>()


}
