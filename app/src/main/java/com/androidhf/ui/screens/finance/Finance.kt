package com.androidhf.ui.screens.finance

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.androidhf.data.enums.SavingsType
import com.androidhf.ui.reuseable.BorderBox
import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.ListXItemsTransactionsMonthly
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.finance.savingcards.SavingCard_Expense2
import com.androidhf.ui.screens.finance.savingcards.SavingCard_Income1
import com.androidhf.ui.screens.finance.savingcards.SavingCard_Income2
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
@ExperimentalMaterialApi
fun FinanceScreen(navHostController: NavHostController, transactionViewModel: TransactionViewModel, savingViewModel: SavingViewModel) {
    UIVar.topBarTitle = "Finance"


    //alsó gombok eltüntetése
    val listState = rememberLazyListState()
    val haptic = LocalView.current


    val isScrolledToBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val isLastItemFullyVisible = lastVisibleItem?.let {
                it.index == layoutInfo.totalItemsCount - 1 &&
                        it.offset + it.size <= layoutInfo.viewportEndOffset
            } ?: false

            val contentLargerThanViewport =
                layoutInfo.totalItemsCount > 0 &&
                        layoutInfo.visibleItemsInfo.size < layoutInfo.totalItemsCount

            isLastItemFullyVisible && contentLargerThanViewport
        }
    }

    //haptic pöccentés ha a legaljára görgettünk
    if(isScrolledToBottom)      haptic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    //
    Box(modifier = Modifier
        .padding(UIVar.Padding)
        .fillMaxSize()
    )
    {

        val savings = savingViewModel.savings.collectAsState()
        LazyColumn(modifier = Modifier
            .fillMaxWidth(),
            state = listState) {
            item {
                BorderBox() {
                    Finance_ui_egyenleg(navHostController,transactionViewModel)
                }
            }
            item {
                Spacer(modifier = Modifier.height(UIVar.Padding))
                Grafikon_init(transactionViewModel)
            }
            item {
                Spacer(modifier = Modifier.height(UIVar.Padding))
                Row(modifier = Modifier.fillMaxWidth()) {
                    BorderBox(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.clickable { navHostController.navigate("FinanceIncome") }) {
                            HeaderText("Bevétel")
                            ListXItemsTransactionsMonthly(transactionViewModel.incomeTransactions.collectAsState(), 40, Color.Green)
                        }
                    }
                    Spacer(modifier = Modifier.width(UIVar.Padding))
                    BorderBox(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.clickable { navHostController.navigate("FinanceExpense") }) {
                            HeaderText("Kiadás")
                            ListXItemsTransactionsMonthly(transactionViewModel.expenseTransactions.collectAsState(), 40, Color.Red)
                        }
                    }
                }
            }
            if (savings.value.isNotEmpty()) {
                this@LazyColumn.items(
                    items = savings.value,
                    key = { it.Id }
                ) { saving ->
                    var visible by remember { mutableStateOf(true) }

                    Spacer(modifier = Modifier.height(UIVar.Padding))
                    AnimatedVisibility(
                        visible = visible,
                        exit = shrinkVertically() + fadeOut(),
                        modifier = Modifier.animateItemPlacement()
                    ) {
                        if(saving.Type == SavingsType.INCOMEGOAL_BYAMOUNT)
                        {
                            SavingCard_Income2(
                                saving = saving,
                                onDismiss = {
                                    visible = false
                                    savingViewModel.deleteSaving(saving)
                                    Log.d("delete","torles")
                                }
                            )
                        }
                        else if(saving.Type == SavingsType.EXPENSEGOAL_BYAMOUNT)
                        {
                            SavingCard_Expense2(
                                saving = saving,
                                onDismiss = {
                                    visible = false
                                }
                            )
                        }
                        else{
                            SavingCard_Income1(
                                saving = saving,
                                onDismiss = {
                                    visible = false
                                },
                                transactionViewModel = transactionViewModel
                            )
                        }
                    }
                    LaunchedEffect(visible) {
                        if (!visible) {
                            haptic.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            delay(300)
                            savingViewModel.deleteSaving(saving)
                        }
                    }
                }
            }
        }



        AnimatedVisibility(
            visible = !isScrolledToBottom,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 300)) + fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Box {
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
    }
}

@Composable
fun Finance_ui_egyenleg(navHostController: NavHostController, transactionViewModel: TransactionViewModel)
{

    val money = transactionViewModel.balance.collectAsState().value
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
fun Grafikon_init(transactionViewModel : TransactionViewModel)
{

    val balance = transactionViewModel.getSortedMoney()
    val list = mutableListOf<Float>()
    var osszeg: Float = 0f
    list.add(0f)
    balance.forEach { item ->
        osszeg += item
        list.add(osszeg)
    }
    val pointsData = list.mapIndexed { index, value -> Point(index.toFloat(), value) }


    val xAxisData = AxisData.Builder()
        .axisStepSize(20.dp)
        .steps((list.size - 1).coerceAtLeast(1))
        .bottomPadding(40.dp)
        .labelData { index -> index.toString() }
        .build()

    val dataMinValue = list.minOrNull() ?: 0f
    val dataMaxValue = list.maxOrNull() ?: 0f

    val yAxisDisplayMin = if (dataMinValue == 0f && dataMaxValue == 0f) -10f else dataMinValue
    val yAxisDisplayMax = if (dataMinValue == 0f && dataMaxValue == 0f) 10f else dataMaxValue


    val yAxisDisplayRange = yAxisDisplayMax - yAxisDisplayMin

    val yAxisData = AxisData.Builder()
        .steps(8)
        .labelData { index ->
            val value = if (yAxisDisplayRange == 0f) {
                yAxisDisplayMin
            } else {
                yAxisDisplayMin + (index.toFloat() / 8.0f) * yAxisDisplayRange
            }
            Math.round(value).toString()
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
                    waveFillColor = WaveFillColor(topColor = UIVar.colorGreen(), bottomColor = UIVar.colorRed()),
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        paddingTop = 20.dp,
        bottomPadding = 0.dp
    )

    WaveChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .border(3.dp, UIVar.boxBorderColor()),
        waveChartData = waveChartData
    )
}

