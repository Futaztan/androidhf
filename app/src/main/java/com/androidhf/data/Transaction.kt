package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.vo.Entity
import java.time.LocalDate
import java.time.LocalTime


//bevétel vagy kiadások
//mutable ha editelni akarjuk valamelyik tranzaikciot akkor változzonm az ui


data class Transaction(
    val _amount : Int,
    val _reason : String,            //tranzakcio rövid leirasa a usertol
    val _date : LocalDate,
    val _time: LocalTime,
    val _category : Category,            //Category-n belüli típus
    val _frequency : Frequency         //milyen gyakran van ez a tranzakcio

)
{
    var amount by mutableStateOf(_amount)
    var reason by mutableStateOf(_reason)
    var date by mutableStateOf(_date)
    var time by mutableStateOf(_time)
    var category by mutableStateOf(_category)
    var frequency by mutableStateOf(_frequency)
}


