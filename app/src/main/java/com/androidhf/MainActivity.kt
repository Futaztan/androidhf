package com.androidhf

import android.os.Build
import android.os.Bundle
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.androidhf.data.Category
import com.androidhf.data.Data
import com.androidhf.data.Data.Osszpenz
import com.androidhf.data.Frequency
import com.androidhf.data.Transaction
import com.androidhf.ui.screens.ai.AIScreen
import com.androidhf.ui.screens.finance.FinanceScreen
import com.androidhf.ui.screens.finance.MoneyExpenseScreen
import com.androidhf.ui.screens.finance.MoneyIncomeScreen
import com.androidhf.ui.screens.finance.MoneySavingsScreen
import com.androidhf.ui.screens.finance.SavingsViewModel
import com.androidhf.ui.screens.home.HomeScreen
import com.androidhf.ui.screens.stock.query.StockChartScreen
import com.androidhf.ui.screens.stock.StockScreen

import com.androidhf.ui.screens.stock.StockViewModel

import com.androidhf.ui.theme.AndroidhfTheme
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidhfTheme {

                listafeltoles()
                val navController = rememberNavController()
                val stockViewModel: StockViewModel = viewModel()
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

                    val financeViewModel: SavingsViewModel = viewModel()
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen() }
                        composable("penzugy") { FinanceScreen(navController, financeViewModel) }
                        composable("stock") { StockScreen(navController, stockViewModel) }
                        composable("stock_detail") { StockChartScreen(stockViewModel) }
                        composable("ai") { AIScreen() }
                        composable("money_income") { MoneyIncomeScreen(navController) }
                        composable("money_expense") { MoneyExpenseScreen(navController) }
                        composable("money_saving") { MoneySavingsScreen(navController, financeViewModel) }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar(
        modifier = Modifier
            .background(Color.Transparent),
        containerColor = Color.White.copy(alpha = 0.2f)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = {
                Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = "Home icon")
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("penzugy") },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_finance), contentDescription = "Finance icon") },
            label = { Text("Pénzügy") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("stock") },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_stocks), contentDescription = "Stocks icon") },
            label = { Text("Stock") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("ai") },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_ai2), contentDescription = "AI icon") },
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

fun listafeltoles()
{
    for (i in 1..25)
    {
        var random = Random.Default
        val transactionplus = Transaction(random.nextInt(200, 2000),"TESZT$i", LocalDate.now().plusDays((i*2).toLong()), LocalTime.now(), Category.FIZETES, Frequency.EGYSZERI)
        val transactionminus = Transaction(random.nextInt(200, 2000),"TESZT$i", LocalDate.now().plusDays((i*2).toLong()), LocalTime.now(), Category.ELOFIZETES, Frequency.EGYSZERI)
        Data.incomesList.add(transactionplus)
        Data.expensesList.add(transactionminus)
    }
    Osszpenz()
}



