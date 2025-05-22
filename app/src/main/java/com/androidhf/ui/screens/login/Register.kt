package com.androidhf.ui.screens.login


import android.content.Context
import android.widget.Toast
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
import com.androidhf.R
import com.androidhf.ui.reuseable.NameField
import com.androidhf.ui.reuseable.Panel
import com.androidhf.ui.reuseable.PasswordField
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.finance.viewmodel.RepetitiveTransactionViewModel
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel
import com.androidhf.ui.screens.login.auth.AuthService
import com.androidhf.ui.screens.stock.StockViewModel


private fun onRegister(
    email: String,
    password1: String,
    password2: String,
    navController: NavController,
    context: Context,
    transactionViewModel: TransactionViewModel,
    reptransViewModel : RepetitiveTransactionViewModel,
    savingViewModel: SavingViewModel,
    stockViewModel : StockViewModel
) {

    if (email.isBlank() || password1.isBlank() || password2.isBlank()) {
        return
    }
    if (!password1.equals(password2)) {
        Toast.makeText(context, R.string.login_passmismatch, Toast.LENGTH_LONG).show();
        return
    }
    AuthService.registerWithEmailAndPassword(email, password1, context) { success ->
        if (success)
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
            navController.navigate("login")
        }


    }


}

@Composable
fun RegisterScreen(navController: NavController,
                   transactionViewModel: TransactionViewModel,
                   reptransViewModel : RepetitiveTransactionViewModel,
                   savingViewModel: SavingViewModel,
                   stockViewModel : StockViewModel) {
    UIVar.topBarTitle = stringResource(id = R.string.login_registration)

    var name by remember { mutableStateOf("") }
    var password1 by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
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
                label = stringResource(id = R.string.login_register),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(UIVar.Padding))
            PasswordField(
                value = password1,
                onChange = { password1 = it },
                // submit = { onRegister(name,password1,navController, context) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(UIVar.Padding))
            PasswordField(
                value = password2,
                onChange = { password2 = it },
                submit = { onRegister(name, password1, password2, navController, context, transactionViewModel,reptransViewModel,savingViewModel,stockViewModel) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(UIVar.Padding))
            Button(onClick = {
                onRegister(
                    name,
                    password1,
                    password2,
                    navController,
                    context,
                    transactionViewModel,reptransViewModel,savingViewModel,stockViewModel
                )
            },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(id = R.string.login_register)) }

        }

    }
}


