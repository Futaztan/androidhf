package com.androidhf.ui.screens.stock


import android.os.Build

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.sp

import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import io.polygon.kotlin.sdk.rest.AggregateDTO

import io.polygon.kotlin.sdk.rest.PolygonRestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockScreen() {
    var showChart by remember { mutableStateOf(false) }

    val stockData = remember { mutableStateListOf<AggregateDTO>()  }
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Stock Screen")
        Button(
            onClick = {
                isLoading = true
                CoroutineScope(Dispatchers.IO).launch { //coroutint indit el a lekérdezéshez
                    try {
                        val POLYGON_API_KEY = PolygonRestClient("j69KmsF2J_JJn1KWl2f_1drc6HT9Cech")
                        val data = stocksAggregatesBars(POLYGON_API_KEY, "2025-03-03", "2025-03-07")
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
            },
            enabled = !isLoading
        )
        {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Load Stock Data")
            }
        }
    }

    if (showChart && stockData != null) {
        LineChartSample(stockData!!)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LineChartSample( results : List<AggregateDTO>) {



    val values = ArrayList<Double>() //lekért adatok
    val dates = ArrayList<String>() //lekért dátumok



    for (i in 0  until results.size)
    {
        values.add(results.get(i).close!!)

        val date =  Instant.ofEpochMilli(results.get(i).timestampMillis!!).atZone(ZoneId.of("America/New_York")).toLocalDate() //milli secundot dátummá teszi
        val formatter = DateTimeFormatter.ofPattern("MM-dd")

        dates.add(date.format(formatter))
    }

    val Lines: List<LineParameters> = listOf(
        LineParameters(
            label = "apple stock",
            data = values,
            lineColor = Color.Red,
            lineType = LineType.CURVED_LINE,
            lineShadow = true,
        )

    )


    Box(Modifier) {
        LineChart(
            modifier = Modifier.fillMaxSize(),
            linesParameters = Lines,
            isGrid = true,
            gridColor = Color.Blue,
            xAxisData = dates,
            animateChart = true,
            showGridWithSpacer = true,
            yAxisStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
            ),
            xAxisStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.W400
            ),
            yAxisRange = 14,
            oneLineChart = false,
            gridOrientation = GridOrientation.VERTICAL
        )
    }
}
