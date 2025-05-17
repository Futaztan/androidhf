package com.androidhf.ui.screens.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.androidhf.ui.reuseable.ListXItemsTransactions
import com.androidhf.ui.reuseable.UIVar

@Composable
fun FinanceIncome(navController: NavHostController) {
    UIVar.topBarTitle = "Incomes List"
    val tViewModel: TransactionViewModel = hiltViewModel()

    var amount by remember { mutableStateOf(false)}
    var category by remember { mutableStateOf(false)}
    var date by remember { mutableStateOf(false)}
    var asc by remember { mutableStateOf(false)}



    val buttonAmountColor = if (amount)
        ButtonDefaults.buttonColors(containerColor = UIVar.onTertColor())
    else
        ButtonDefaults.buttonColors()
    val buttonCatColor = if (category)
        ButtonDefaults.buttonColors(containerColor = UIVar.onTertColor())
    else
        ButtonDefaults.buttonColors()
    val buttonDateColor = if (date)
        ButtonDefaults.buttonColors(containerColor = UIVar.onTertColor())
    else
        ButtonDefaults.buttonColors()

    Box(modifier = Modifier.fillMaxSize())
    {
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth())
        {
            Row(modifier = Modifier, Arrangement.SpaceAround) {
                Spacer(modifier = Modifier.width(UIVar.Padding))
                Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                    Text(text = "Vissza")
                }
                Spacer(modifier = Modifier.width(UIVar.Padding))
                Button(onClick = { navController.navigate("FinanceExpense") }, modifier = Modifier.weight(1f)) {
                    Text(text = "Kiadások ->")
                }
                Spacer(modifier = Modifier.width(UIVar.Padding))
            }
        }
    }

    Column (modifier = Modifier.padding(UIVar.Padding)) {
        Text(text = "Sort by:", modifier = Modifier.padding(bottom = UIVar.Padding), fontSize = UIVar.HeaderText)
        LazyRow {
            item {
                Button(onClick = {
                    amount = !amount
                    category = false
                    date = false
                }, colors = buttonAmountColor) {
                    Text(text = "Amount")
                }
                Spacer(modifier = Modifier.width(UIVar.Padding))
                Button(onClick = {
                    category = !category
                    amount = false
                    date = false
                }, colors = buttonCatColor) {
                    Text(text = "Category")
                }
                Spacer(modifier = Modifier.width(UIVar.Padding))
                Button(onClick = {
                    date = !date
                    amount = false
                    category = false
                }, colors = buttonDateColor) {
                    Text(text = "Date")
                }
            }
        }
        if (amount) {
            Column() {
                Button(onClick = {
                    asc = !asc
                }) {
                    Text(text = "Ascend/Descend")
                }
                Spacer(modifier = Modifier.height(UIVar.Padding))
                if (asc)
                {
                    ListXItemsTransactions(null, tViewModel.sortIncomeTransactionsByAmount(), -1, UIVar.colorGreen())
                }
                else ListXItemsTransactions(null,tViewModel.sortIncomeTransactionsByAmount(), -1, UIVar.colorGreen(), reversed = true)
            }
        }
        else if (category) {
            Spacer(modifier = Modifier.height(UIVar.Padding))
            ListXItemsTransactions(null, tViewModel.sortIncomeByCategory(), -1, UIVar.colorGreen())
        }
        else if (date) {
            Button(onClick = {
                asc = !asc
            }) {
                Text(text = "Ascend/Descend")
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
            if (asc)
            {
                ListXItemsTransactions(null, tViewModel.sortIncomeTransactionsByDate(asc = true), -1, UIVar.colorGreen())
            }
            else ListXItemsTransactions(null,tViewModel.sortIncomeTransactionsByDate(), -1, UIVar.colorGreen())
        }
        else
        {
            Spacer(modifier = Modifier.height(UIVar.Padding))
            ListXItemsTransactions(tViewModel.incomeTransactions.collectAsState(), null, -1, UIVar.colorGreen(), reversed = true)
        }
    }
}