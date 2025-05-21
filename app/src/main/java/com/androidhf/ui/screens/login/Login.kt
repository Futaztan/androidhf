package com.androidhf.ui.screens.login

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidhf.ui.reuseable.NameField
import com.androidhf.ui.reuseable.PasswordField
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.login.auth.AuthService
import com.androidhf.ui.reuseable.Panel
import com.androidhf.R
import com.androidhf.ui.screens.finance.viewmodel.RepetitiveTransactionViewModel
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel
import com.androidhf.ui.screens.stock.StockViewModel


private fun onLogin(email: String, password : String, navController: NavController, context: Context,
                    transactionViewModel: TransactionViewModel,reptransViewModel: RepetitiveTransactionViewModel,savingViewModel: SavingViewModel,stockViewModel: StockViewModel)
{

    AuthService.getUserEmail()
    AuthService.loginWithEmailAndPassword(email,password, context){ success->
        if(success)

        {
            transactionViewModel.deleteAll()
            reptransViewModel.deleteAll()
            savingViewModel.deleteAll()
            stockViewModel.deleteAllStock()
            stockViewModel.deleteAllCompany()
            transactionViewModel.loadTransactions()
            reptransViewModel.loadRepTransactions()
            savingViewModel.loadSavings()
            stockViewModel.loadStock()
            stockViewModel.loadCompany()
            navController.navigate("home")

        }


    }


}

@Composable
fun LoginScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    reptransViewModel: RepetitiveTransactionViewModel,
    savingViewModel: SavingViewModel,
    stockViewModel: StockViewModel
) {
    UIVar.topBarTitle = "Login"

    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    Panel(cornerRadius = 0.dp) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            NameField(
                value = name,
                onChange = { name = it },
                label = stringResource(id = R.string.login_user),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(UIVar.Padding))
            PasswordField(
                value = password,
                onChange = { password=it },
                submit = {onLogin(name,password,navController, context,transactionViewModel,reptransViewModel,savingViewModel,stockViewModel)},
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.login_pass)
            )
            Spacer(Modifier.padding(UIVar.Padding))
            Button(onClick = {
                onLogin(name,password,navController,context,transactionViewModel,reptransViewModel,savingViewModel,stockViewModel)},
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(id = R.string.login_user)) }
            Button(onClick ={
                navController.navigate("register")},
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(id = R.string.login_register))}

        }

    }
}


