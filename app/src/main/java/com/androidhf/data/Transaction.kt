package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate
import java.time.LocalTime


//bevétel vagy kiadások
//mutable ha editelni akarjuk valamelyik tranzaikciot akkor változzonm az ui


class Transaction(
   _amount : Int,
   _reason : String,            //tranzakcio rövid leirasa a usertol
   _date : LocalDate,
   _time: LocalTime,
   _category : Category,            //Category-n belüli típus
    _frequency : Frequency         //milyen gyakran van ez a tranzakcio

)
{
    var amount by mutableStateOf(_amount)
    var reason by mutableStateOf(_reason)
    var date by mutableStateOf(_date)
    var time by mutableStateOf(_time)
    var category by mutableStateOf(_category)
    var frequency by mutableStateOf(_frequency)
}