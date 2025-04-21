package com.androidhf.ui.screens.finance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import co.yml.charts.ui.wavechart.WaveChart
import co.yml.charts.ui.wavechart.model.Wave
import co.yml.charts.ui.wavechart.model.WaveChartData
import co.yml.charts.ui.wavechart.model.WaveFillColor
import co.yml.charts.ui.wavechart.model.WavePlotData
import com.androidhf.data.Data
import com.androidhf.ui.reuseable.BorderBox
import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.LastXItemsTransactions
import com.androidhf.ui.reuseable.UIVariables
import kotlin.math.max
import kotlin.math.min
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.androidhf.ui.reuseable.LastXItemsTransactionsMonthly
import com.androidhf.ui.screens.finance.savingcards.SavingCard_Income1
import kotlinx.coroutines.delay


@OptIn(ExperimentalFoundationApi::class)
@Composable
@ExperimentalMaterialApi
fun FinanceScreen(navHostController: NavHostController, viewModel: SavingsViewModel) {
    Data.topBarTitle = "Finance"
    Box(modifier = Modifier
        .padding(UIVariables.Padding)
        .fillMaxSize()
    )
    {
        //val alma = Savings(50000, LocalDate.of(2025,4,12), LocalDate.now().plusDays(2), SavingsType.INCOMEGOAL_BYTIME, "Title", "Description", 20000)
        //savingsList.clear()//TODO: ez majd nem kell ide, most csak azért van, hogyha újra rajzolódik akkor ne vegye fel megint
        //savingsList.add(alma)
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(bottom = 56.dp)) {
            item {
                BorderBox() {
                    Finance_ui_egyenleg(navHostController)
                }
            }
            item {
                Spacer(modifier = Modifier.height(UIVariables.Padding))
                Grafikon_init()
            }
            item {
                Spacer(modifier = Modifier.height(UIVariables.Padding))
                Row(modifier = Modifier.fillMaxWidth()) {
                    BorderBox(modifier = Modifier.weight(1f)) {
                        Column {
                            HeaderText("Bevétel")
                            LastXItemsTransactionsMonthly(Data.incomesList, 40, Color.Green)
                        }
                    }
                    Spacer(modifier = Modifier.width(UIVariables.Padding))
                    BorderBox(modifier = Modifier.weight(1f)) {
                        Column {
                            HeaderText("Kiadás")
                            LastXItemsTransactionsMonthly(Data.expensesList, 40, Color.Red)
                        }
                    }
                }
            }
            if (viewModel.savingsList.isNotEmpty()) {
                item {
                    this@LazyColumn.items(
                        items = viewModel.savingsList,
                        key = { it.id }
                    ) { saving ->
                        var visible by remember { mutableStateOf(true) }

                        Spacer(modifier = Modifier.height(UIVariables.Padding))
                        AnimatedVisibility(
                            visible = visible,
                            exit = shrinkVertically() + fadeOut(),
                            modifier = Modifier.animateItemPlacement()
                        ) {
                            SavingCard_Income1(
                                saving = saving,
                                onDismiss = {
                                    visible = false
                                }
                            )
                        }
                        LaunchedEffect(visible) {
                            if (!visible) {
                                delay(300)
                                viewModel.removeSaving(saving)
                            }
                        }
                    }
                }
            }
        }



        Button(onClick = {navHostController.navigate("money_income")},
            modifier = Modifier.align(Alignment.BottomStart)
        ) { Text("Bevétel") }

        Button(onClick = {navHostController.navigate("money_saving")},
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { Text("Takarék???") }

        Button(onClick = {navHostController.navigate("money_expense")},
            modifier = Modifier.align(Alignment.BottomEnd)
        ) { Text("Kiadás") }
    }
}

@Composable
fun Finance_ui_egyenleg(navHostController: NavHostController)
{
    val money = Data.osszpenz
    Column(
        modifier = Modifier
    )
    {
        Row ( modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
        )
        {
            HeaderText("Egyenleg: ")
            if(money < 0) Text("$money Ft", color = Color.Red)
            else if(money > 0) Text("$money Ft", color = Color.Green)
            else Text("$money Ft", color = Color.Black)

        }
    }
}

@Composable
fun Finance_ui_bevetel(navHostController: NavHostController)
{
    val bevetellist = Data.incomesList
    Column(modifier = Modifier.fillMaxWidth()) {
        HeaderText("Bevetelek")
        if(bevetellist.size >= 1)
        {
            for (i in bevetellist.size-1 downTo maxOf(0, bevetellist.size-6))
            {
                Text("+${bevetellist[i].amount} Ft", color = Color.Green)
            }
        }
    }
}

@Composable
fun Finance_ui_kiadas(navHostController: NavHostController)
{
    val kiadaslist = Data.expensesList
    Column(modifier = Modifier.fillMaxWidth()) {
        HeaderText("Kiadas")
        if(kiadaslist.size >= 1)
        {
            for (i in kiadaslist.size-1 downTo maxOf(0, kiadaslist.size-6))
            {
                Text("-${kiadaslist[i].amount} Ft", color = Color.Red)
            }
        }

    }
}

@Composable
fun Grafikon_init()
{
    val balance = Data.calculateBalanceChangesSimple()
    val pointsData = balance.mapIndexed { index, value -> Point(index.toFloat(), value.toFloat()) }


    val xAxisData = AxisData.Builder()
        .axisStepSize(20.dp)
        .steps(balance.size-1)
        .bottomPadding(40.dp)
        .labelData { index -> index.toString() }
        .build()

    val yStepSize = 8
    val rawMin = balance.minOrNull() ?: 0
    val rawMax = balance.maxOrNull() ?: 0
    val minRange = min(0, rawMin)
    val maxRange = max(0, rawMax)

    val range = maxRange - minRange
    val step = range / yStepSize

    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .labelData { index ->
            String.format("%d", minRange + (index * step))
        }
        .build()

    val waveChartData = WaveChartData(
        wavePlotData = WavePlotData(
            lines = listOf(
                Wave(
                    dataPoints = pointsData,
                    waveStyle = LineStyle(color = Color.Black),
                    selectionHighlightPoint = SelectionHighlightPoint(),
                    shadowUnderLine = ShadowUnderLine(),
                    selectionHighlightPopUp = SelectionHighlightPopUp(),
                    waveFillColor = WaveFillColor(topColor = Color.Green, bottomColor = Color.Red),
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines()
    )

    WaveChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        waveChartData = waveChartData
    )
}

