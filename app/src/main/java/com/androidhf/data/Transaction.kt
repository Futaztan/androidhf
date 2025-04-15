package com.androidhf.data

import java.time.LocalDate

data class Transaction(
    val amount : Double,
    val reason : String,
    val date : LocalDate,
    val category : Category
)