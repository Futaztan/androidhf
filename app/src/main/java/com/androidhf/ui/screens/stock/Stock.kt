package com.androidhf.ui.screens.stock


import android.icu.text.CaseMap.Title
import android.os.Build

import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.BottomSheetScaffold

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment


import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController

import com.androidhf.R

import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.Panel

import com.androidhf.ui.reuseable.UIVar

import com.androidhf.ui.screens.stock.query.stocksAggregatesBars
import io.polygon.kotlin.sdk.rest.AggregateDTO

import io.polygon.kotlin.sdk.rest.PolygonRestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import com.androidhf.ui.screens.stock.query.LineChartSample
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockScreen(navController: NavController, stockViewModel: StockViewModel) {
    UIVar.topBarTitle = "Stock"
    var showChart by remember { mutableStateOf(false) }

    val stockData = remember { mutableStateListOf<AggregateDTO>()  }
    var isLoading by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val list = mutableListOf("Apple", "Google", "Tesla", "Amazon", "Microsoft", "Meta", "Netflix", "Nvidia", "Blackrock")
    val codes = mutableListOf("AAPL", "GOOGL", "TSLA", "AMZN", "MSFT", "META", "NFLX", "NVDA", "BLK")
    var currentCompanyName by remember { mutableStateOf("") }

    var showBottomWindow = remember { mutableStateOf(false) }

    fun stockQuery(company: String) {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val POLYGON_API_KEY = PolygonRestClient("j69KmsF2J_JJn1KWl2f_1drc6HT9Cech")
                val now: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val old: String = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val data = stocksAggregatesBars(POLYGON_API_KEY, company, old, now)
                withContext(Dispatchers.Main) {
                    stockData.clear() // Clear before adding new data
                    stockData.addAll(data.results)
                    showChart = true

                    // Set company name based on code
                    val index = codes.indexOf(company)
                    if (index != -1) {
                        currentCompanyName = list[index]
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }





    Box(modifier = Modifier.fillMaxSize()){

        Box(modifier = Modifier.align(Alignment.BottomCenter)){
            Column(modifier = Modifier.fillMaxWidth()){

                LazyRow {
                    itemsIndexed(list, key = { index, _ -> UUID.randomUUID().toString() }) { index, item ->
                        if (index == 0) {
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                        }

                        Button(onClick = {
                            // First clear previous data
                            stockData.clear()

                            // Query the stock data for the selected company
                            stockQuery(codes[index])

                            // Show the bottom window
                            showBottomWindow.value = true
                        }, enabled = !isLoading) {
                            Text(item)
                        }

                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()){

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Keresés...")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(6f)
                    )
                    Spacer(modifier = Modifier.width(UIVar.Padding) )
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(2f)
                            .align(Alignment.CenterVertically)
                    ){
                        Text("Keresés")
                    }

                }
            }
        }
    }
    if(showBottomWindow.value) bottomwindow(
        showBottom = showBottomWindow,
        stockData = stockData,
        companyName = currentCompanyName,
        isLoading = isLoading
    )


    Column(modifier = Modifier.fillMaxSize()) {
        HeaderText("Jelenlegi befektetések:")





    }

    if (showChart && stockData != null) {
        stockViewModel.setData(stockData)
        //navController.navigate("stock_detail")
    }
}

@Composable
fun investbox(stockData: List<AggregateDTO>){
    Box(modifier = Modifier
        .fillMaxWidth()
    ){
        Row(modifier = Modifier.fillMaxWidth()){
            Text("Apple - AAPL")

        }

    }
}

@Composable
fun bottomwindow(
    showBottom: MutableState<Boolean>,
    stockData: List<AggregateDTO>,
    companyName: String,
    isLoading: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Panel(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(), centerItems = false) {
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                Row {
                    if(!isLoading){
                        Text("%.2f USD".format(stockData.last().close ?: 0.0)) //TODO: ÁT KELL VÁLTANI FORINTRA
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                        val prev = stockData.first().close;
                        val now = stockData.last().close;
                        val perc = 100- (prev?.div(now!!))?.times(100)!!

                        Text("%.2f%%".format(perc)) //TODO: GYURI szín
                    }

                }
            }
            Column {
                HeaderText(companyName)

                // Show loading or chart
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (stockData.isNotEmpty()) {
                    LineChartSample(stockData, companyName + " stock")
                }

                Button(onClick = {}) { Text("button1") }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { showBottom.value = false }, modifier = Modifier
                        .weight(3f)
                        .align(Alignment.CenterVertically)) { Text("Mégse") }
                    Spacer(modifier = Modifier.width(UIVar.Padding))
                    Button(onClick = {}, modifier = Modifier
                        .weight(3f)
                        .align(Alignment.CenterVertically),
                        enabled = !isLoading ) { // Disable during loading
                        Text("Vásárlás")
                    }
                    Spacer(modifier = Modifier.width(UIVar.Padding))
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notfav_48),
                            contentDescription = "Not favourite icon",
                            modifier = Modifier.weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniStockChart(stockData: List<AggregateDTO>) {
    if (stockData.isEmpty()) return

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height
        val values = stockData.mapNotNull { it.close }

        if (values.isNotEmpty()) {
            val minValue = values.minOrNull() ?: 0.0
            val maxValue = values.maxOrNull() ?: 1.0
            val valueRange = maxValue - minValue

            val path = Path()
            values.forEachIndexed { index, value ->
                val x = width * index / (values.size - 1)
                val y = height * (1 - (value - minValue) / valueRange)

                if (index == 0) {
                    path.moveTo(x.toFloat(), y.toFloat())
                } else {
                    path.lineTo(x.toFloat(), y.toFloat())
                }
            }

            // Színezés a változás alapján
            val firstValue = values.first()
            val lastValue = values.last()
            val lineColor = if (lastValue >= firstValue) Color(0xFF4CAF50) else Color(0xFFFF5252)

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }
}





