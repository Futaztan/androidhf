package com.androidhf.ui.screens.stock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.polygon.kotlin.sdk.rest.AggregateDTO

//viewmodel hogy át lehessen vinni adatot screen között
class StockViewModel : ViewModel() {
     var stockData by mutableStateOf<List<AggregateDTO>?>(null)
         private set


    fun setData(data: List<AggregateDTO>) {
        stockData = data
    }
}