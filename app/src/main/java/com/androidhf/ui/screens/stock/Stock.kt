package com.androidhf.ui.screens.stock


import android.os.Build

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


import androidx.compose.ui.Modifier

import androidx.navigation.NavController
import com.androidhf.ui.reuseable.UIVar

import com.androidhf.ui.screens.stock.query.stocksAggregatesBars
import io.polygon.kotlin.sdk.rest.AggregateDTO

import io.polygon.kotlin.sdk.rest.PolygonRestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockScreen(navController: NavController, stockViewModel: StockViewModel) {
    UIVar.topBarTitle = "Stock"
    var showChart by remember { mutableStateOf(false) }

    val stockData = remember { mutableStateListOf<AggregateDTO>()  }
    var isLoading by remember { mutableStateOf(false) }

    fun stockQuery(company : String)
    {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch { //coroutint indit el a lekérdezéshez
            try {
                val POLYGON_API_KEY = PolygonRestClient("j69KmsF2J_JJn1KWl2f_1drc6HT9Cech")
                val data = stocksAggregatesBars(POLYGON_API_KEY,company, "2025-03-03", "2025-03-07")
                withContext(Dispatchers.Main) {
                    stockData.addAll(data.results)
                    showChart = true
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Stock Screen")
        Button(onClick = {stockQuery("AAPL")}, enabled = !isLoading) { Text("Apple")}
        Button(onClick = { stockQuery("MSFT")}, enabled = !isLoading) { Text("Microsoft")}
        Button(onClick = { stockQuery("BLK")}, enabled = !isLoading) { Text("Blackrock") }



        if (isLoading) {
                CircularProgressIndicator()
        }

    }

    if (showChart && stockData != null) {
        stockViewModel.setData(stockData)
        navController.navigate("stock_detail")
    }
}




