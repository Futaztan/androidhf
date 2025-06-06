package com.androidhf

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.ai.AIScreen
import com.androidhf.ui.screens.finance.detail.FinanceExpense
import com.androidhf.ui.screens.finance.detail.FinanceIncome
import com.androidhf.ui.screens.finance.FinanceScreen
import com.androidhf.ui.screens.finance.money.MoneyExpenseScreen
import com.androidhf.ui.screens.finance.MoneyIncomeScreen
import com.androidhf.ui.screens.finance.money.MoneySavingsScreen
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel
import com.androidhf.ui.screens.finance.dailycheck.DailyWorker
import com.androidhf.ui.screens.finance.viewmodel.RepetitiveTransactionViewModel
import com.androidhf.ui.screens.home.HomeScreen
import com.androidhf.ui.screens.stock.query.StockChartScreen
import com.androidhf.ui.screens.stock.StockScreen


import com.androidhf.ui.theme.AndroidhfTheme
import androidx.compose.ui.res.stringResource
import com.androidhf.ui.screens.ai.AIViewModel

import com.androidhf.ui.screens.login.LoginScreen
import com.androidhf.ui.screens.login.RegisterScreen
import com.androidhf.ui.screens.login.auth.AuthService
import com.androidhf.ui.screens.stock.StockViewModel

import com.androidhf.ui.screens.user.UserScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)

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


        setContent {

            AndroidhfTheme {
                val savingViewModel: SavingViewModel = hiltViewModel()
                val transactionViewModel: TransactionViewModel = hiltViewModel()
                val navController = rememberNavController()
                val stockViewModel: StockViewModel = hiltViewModel()
                val aiViewModel : AIViewModel = hiltViewModel()
                val reptransViewModel : RepetitiveTransactionViewModel = hiltViewModel()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(navController)
                    },
                    topBar =
                    {
                        CustomTopAppBar(navController)
                    }
                ) { innerPadding ->


                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") { LoginScreen(navController,transactionViewModel,reptransViewModel,savingViewModel,stockViewModel) }
                        composable("register") { RegisterScreen(navController,transactionViewModel,reptransViewModel,savingViewModel,stockViewModel) }
                        composable("user") { UserScreen(navController)}
                        composable("home") { HomeScreen(transactionViewModel,savingViewModel, reptransViewModel) }
                        composable("penzugy") { FinanceScreen(navController,transactionViewModel,savingViewModel) }
                        composable("stock") { StockScreen(navController,stockViewModel) }
                        composable("stock_detail") { StockChartScreen(stockViewModel) }

                        composable("ai") { AIScreen(aiViewModel) }
                        composable("money_income") { MoneyIncomeScreen(navController,transactionViewModel, reptransViewModel,savingViewModel) }
                        composable("money_expense") { MoneyExpenseScreen(navController,transactionViewModel,reptransViewModel,savingViewModel) }
                        composable("money_saving") { MoneySavingsScreen(navController,transactionViewModel, savingViewModel) }

                        composable("FinanceIncome") { FinanceIncome(navController,transactionViewModel) }
                        composable("FinanceExpense") { FinanceExpense(navController,transactionViewModel) }
                    }
                    val balance = transactionViewModel.balance
                    LaunchedEffect(key1 = Unit) {
                        delay(1250) //Idő amíg betölti az adatbázist
                        savingViewModel.dateChecker(balance.first())

                    }

                    lifecycleScope.launchWhenCreated {
                        savingViewModel.messages.collect { msg ->
                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                            delay(2500)
                        }
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
            label = { Text(stringResource(id = R.string.mainactivity_home)) }
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
            label = { Text(stringResource(id = R.string.mainactivity_finance)) }
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
            label = { Text(stringResource(id = R.string.mainactivity_stock)) }
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
fun CustomTopAppBar(navController: NavHostController) {
    val haptic = LocalView.current
    var showDropdown by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(UIVar.topBarTitle) },
        actions = {
            IconButton(
                onClick =
                    {
                        haptic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        showDropdown = !showDropdown
                        /*
                        if(AuthService.isLoggedIn()) navController.navigate("user")
                        else navController.navigate("login")
                        */
                    })
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_account),
                    contentDescription = "Account icon"
                )
            }
            ProfileDropdown(
                navController = navController,
                visible = showDropdown,
                onDismissRequest = { showDropdown = false }
            )
        }
    )
}

@Composable
fun ProfileDropdown(
    navController: NavHostController,
    visible: Boolean,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        expanded = visible,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(x = 0.dp, y = 8.dp),
        properties = PopupProperties(focusable = true)
    ) {
        if (AuthService.isLoggedIn()) {
            DropdownMenuItem(
                text = { Text("Profile") },
                onClick = {
                    navController.navigate("user")
                    onDismissRequest()
                }
            )
            DropdownMenuItem(
                text = { Text("Logout") },
                onClick = {
                    AuthService.logOut()
                    navController.navigate("home")
                    onDismissRequest()
                }
            )
        } else {
            DropdownMenuItem(
                text = { Text("Login") },
                onClick = {
                    navController.navigate("login")
                    onDismissRequest()
                }
            )
            DropdownMenuItem(
                text = { Text("Register") },
                onClick = {
                    navController.navigate("register")
                    onDismissRequest()
                }
            )
        }
    }
}

fun listafeltoles() {
    /*
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



