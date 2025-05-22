package com.androidhf.ui.screens.stock.uielements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.polygon.kotlin.sdk.rest.AggregateDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MiniStockChart(stockData: List<AggregateDTO>, height: Dp = 0.dp) {
    if (stockData.isEmpty()) return

    val values = stockData.mapNotNull { it.close }
    if (values.isEmpty()) return

    val minValue = values.minOrNull() ?: 0.0
    val maxValue = values.maxOrNull() ?: 1.0

    val today = LocalDate.now()
    val weekAgo = today.minusDays(7)

    val firstDate = weekAgo.format(DateTimeFormatter.ofPattern("MM.dd"))
    val lastDate = today.format(DateTimeFormatter.ofPattern("MM.dd"))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(start = 4.dp, end = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 24.dp, end = 4.dp, top = 16.dp, bottom = 24.dp) // helyet hagyunk a feliratoknak
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height
                val valueRange = maxValue - minValue

                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, height),
                    end = Offset(width, height),
                    strokeWidth = 1.dp.toPx()
                )

                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, 0f),
                    end = Offset(0f, height),
                    strokeWidth = 1.dp.toPx()
                )

                if (values.isNotEmpty()) {
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

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(top = 12.dp, bottom = 12.dp)
                .height(height - 40.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "%.0f".format(maxValue),
                style = TextStyle(fontSize = 8.sp),
                color = Color.Gray
            )

            Text(
                text = "%.0f".format(minValue),
                style = TextStyle(fontSize = 8.sp),
                color = Color.Gray
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 4.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = firstDate,
                style = TextStyle(fontSize = 8.sp),
                color = Color.Gray
            )

            Text(
                text = lastDate,
                style = TextStyle(fontSize = 8.sp),
                color = Color.Gray
            )
        }
    }
}