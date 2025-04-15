package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate


//bevétel vagy kiadások
//mutable ha editelni akarjuk valamelyik tranzaikciot akkor változzonm az ui


class Transaction(
   _amount : Double,
   _reason : String,
   _date : LocalDate,
   _category : Category
)
{
    var amount by mutableStateOf(_amount)
    var reason by mutableStateOf(_reason)
    var date by mutableStateOf(_date)
    var category by mutableStateOf(_category)
}