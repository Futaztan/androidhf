package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object Datas {
    var osszpenz by mutableStateOf(0.0)
    var incomesList = ArrayList<Double>()
    var expensesList = ArrayList<Double>()


}
