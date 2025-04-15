package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.setValue

object Data {
    var incomesList = ArrayList<Transaction>()
    var expensesList = ArrayList<Transaction>()
    var osszpenz by mutableDoubleStateOf(0.0)



}
