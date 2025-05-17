package com.androidhf.ui.screens.stock.query

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.androidhf.ui.screens.stock.StockViewModel
import io.polygon.kotlin.sdk.rest.AggregateDTO
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockChartScreen()
{
    val stockViewModel : StockViewModel = hiltViewModel()
    LineChartSample(stockViewModel.stockData!!, "")
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LineChartSample( results : List<AggregateDTO>, label : String) {

    if(results.isEmpty()){
        return
    }

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
            label = label,
            data = values,
            lineColor = Color.Red,
            lineType = LineType.CURVED_LINE,
            lineShadow = true,
        )

    )


    Box(Modifier) {
        LineChart(
            modifier = Modifier.height(400.dp),
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