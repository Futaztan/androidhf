package com.androidhf

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.androidhf.data.Category
import com.androidhf.data.Data

import com.androidhf.data.Frequency
import com.androidhf.data.Savings
import com.androidhf.data.SavingsType
import com.androidhf.data.Transaction
import com.androidhf.ui.screens.ai.AIScreen
import com.androidhf.ui.screens.ai.AIViewModel
import com.androidhf.ui.screens.finance.FinanceScreen
import com.androidhf.ui.screens.finance.MoneyExpenseScreen
import com.androidhf.ui.screens.finance.MoneyIncomeScreen
import com.androidhf.ui.screens.finance.MoneySavingsScreen

import com.androidhf.ui.screens.finance.everyXtime.DailyWorker
import com.androidhf.ui.screens.home.HomeScreen
import com.androidhf.ui.screens.stock.query.StockChartScreen
import com.androidhf.ui.screens.stock.StockScreen

import com.androidhf.ui.screens.stock.StockViewModel

import com.androidhf.ui.theme.AndroidhfTheme
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    @RequiresApi(Build.VERSION_CODES.O)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val uploadWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<DailyWorker>(24, TimeUnit.HOURS)
                .build()
        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "every24hours",
                ExistingPeriodicWorkPolicy.KEEP,
                uploadWorkRequest
            )

        Data.init(this)
        CoroutineScope(Dispatchers.IO).launch {
            Data.loadTransactions()
            Data.loadSaves()
        }


        setContent {
            AndroidhfTheme {
                /*
                var elso by remember { mutableStateOf(true) }

                if (elso) {
                    listafeltoles()
                    elso = false
                } */

                val navController = rememberNavController()
                val stockViewModel: StockViewModel = viewModel()
                val aIViewModel: AIViewModel = viewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(navController)
                    },
                    topBar =
                        {
                            CustomTopAppBar()
                        }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen(/*financeViewModel*/) }
                        composable("penzugy") { FinanceScreen(navController /*financeViewModel*/) }
                        composable("stock") { StockScreen(navController, stockViewModel) }
                        composable("stock_detail") { StockChartScreen(stockViewModel) }

                        composable("ai") { AIScreen(aIViewModel) }
                        composable("money_income") { MoneyIncomeScreen(navController/*, financeViewModel*/) }
                        composable("money_expense") { MoneyExpenseScreen(navController/*, financeViewModel*/) }
                        composable("money_saving") { MoneySavingsScreen(navController /*financeViewModel*/) }

                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val haptic = LocalView.current

    NavigationBar(
        modifier = Modifier
            .background(Color.Transparent),
        containerColor = Color.White.copy(alpha = 0.2f)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home")
                haptic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home icon"
                )
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("penzugy")
                haptic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_finance),
                    contentDescription = "Finance icon"
                )
            },
            label = { Text("Pénzügy") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("stock")
                haptic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_stocks),
                    contentDescription = "Stocks icon"
                )
            },
            label = { Text("Stock") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("ai")
                haptic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ai2),
                    contentDescription = "AI icon"
                )
            },
            label = { Text("AI") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar() {
    TopAppBar(
        title = { Text(Data.topBarTitle) },
        actions = {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_account),
                    contentDescription = "Account icon"
                )
            }
        }
    )
}

fun listafeltoles() {
    for (i in 1..25) {
        var random = Random.Default
        val transactionplus = Transaction(
            random.nextInt(200, 2000),
            "TESZT$i",
            LocalDate.now().plusDays((i * 2).toLong()),
            LocalTime.now(),
            Category.FIZETES,
            Frequency.EGYSZERI
        )
        val transactionminus = Transaction(
            random.nextInt(-2000, -200),
            "TESZT$i",
            LocalDate.now().plusDays((i * 2).toLong()),
            LocalTime.now(),
            Category.ELOFIZETES,
            Frequency.EGYSZERI
        )
        CoroutineScope(Dispatchers.IO).launch {
            Data.addTransaction(transactionplus)
            Data.addTransaction(transactionminus)
        }
    }
    /*
    val alma = Savings(
        50000,
        LocalDate.of(2025, 4, 12),
        LocalDate.now().plusDays(2),
        SavingsType.INCOMEGOAL_BYTIME,
        "Title",
        "Description",
        20000
    )
    Data.savingsList.add(alma)
    val alma1 = Savings(
        40000,
        LocalDate.of(2025, 4, 11),
        LocalDate.now().plusDays(4),
        SavingsType.INCOMEGOAL_BYTIME,
        "Title1",
        "Description1",
        10000
    )
    Data.savingsList.add(alma1)
    val alma2 = Savings(
        30000,
        LocalDate.of(2025, 4, 5),
        LocalDate.now().plusDays(5),
        SavingsType.INCOMEGOAL_BYTIME,
        "Title2",
        "Description2",
        20000
    )
    Data.savingsList.add(alma2)
    val alma3 = Savings(
        20000,
        LocalDate.of(2025, 4, 20),
        LocalDate.now().plusDays(8),
        SavingsType.INCOMEGOAL_BYTIME,
        "Title3",
        "Description3",
        20000
    )
    Data.savingsList.add(alma3)
    val alma4 = Savings(
        10000,
        LocalDate.of(2025, 4, 15),
        LocalDate.now().plusDays(25),
        SavingsType.INCOMEGOAL_BYTIME,
        "Title4",
        "Description4",
        20000
    )
    Data.savingsList.add(alma4)
    */
}



