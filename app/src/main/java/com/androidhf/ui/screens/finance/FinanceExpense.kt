package com.androidhf.ui.screens.finance

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.androidhf.data.datatypes.Transaction
import com.androidhf.ui.reuseable.ListXItemsTransactions
import com.androidhf.ui.reuseable.UIVar

@Composable
fun FinanceExpense(navController: NavHostController) {
    UIVar.topBarTitle = "Expense List"
    val tViewModel: TransactionViewModel = hiltViewModel()

    var amount by remember { mutableStateOf(false)}
    var category by remember { mutableStateOf(false)}
    var date by remember { mutableStateOf(false)}
    var search by remember { mutableStateOf(false) }
    var asc by remember { mutableStateOf(false)}

    var input by remember { mutableStateOf("")}

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
    val buttonSearchColor = if (search)
        ButtonDefaults.buttonColors(containerColor = UIVar.onTertColor())
    else
        ButtonDefaults.buttonColors()

    val transactions = tViewModel.expenseTransactions.collectAsState()
    val currentTransactions = transactions.value

    val list: List<Transaction> = remember(input, currentTransactions) {
        if (input.isEmpty()) {
            currentTransactions
        } else {
            val filteredList = tViewModel.incomeContainsList(input)
            filteredList
        }
    }

    Column (modifier = Modifier.padding(UIVar.Padding).verticalScroll(rememberScrollState())) {
        Text(text = "Sort by:", modifier = Modifier.padding(bottom = UIVar.Padding), fontSize = UIVar.HeaderText)
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                amount = !amount
                category = false
                date = false
            }, colors = buttonAmountColor) {
                Text(text = "Amount")
            }
            Button(onClick = {
                category = !category
                amount = false
                date = false
            }, colors = buttonCatColor) {
                Text(text = "Category")
            }
            Button(onClick = {
                date = !date
                amount = false
                category = false
            }, colors = buttonDateColor) {
                Text(text = "Date")
            }
            Button(onClick = {
                search = !search
                if(!search)
                {
                    input = ""
                }
                amount = false
                category = false
                date = false
            }, colors = buttonSearchColor) {
                Text(text = "Search")
            }
        }
        if (search)
        {
            Spacer(modifier = Modifier.height(UIVar.Padding))
            TextField(
                value = input,
                onValueChange = {
                    input = it
                },
                placeholder = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(UIVar.Padding))
            if (!date && !category && !amount)
            {
                ListXItemsTransactions(null, list, -1, UIVar.colorRed())
            }
        }
        if (amount) {
            Button(onClick = {
                asc = !asc
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Ascend/Descend")
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
            if (asc)
            {
                ListXItemsTransactions(null, tViewModel.sortTransactionsByAmount(list = list), -1, UIVar.colorRed())
            }
            else ListXItemsTransactions(null,tViewModel.sortTransactionsByAmount(list = list), -1, UIVar.colorRed(), reversed = true)
        }
        else if (category) {
            Spacer(modifier = Modifier.height(UIVar.Padding))
            ListXItemsTransactions(null, tViewModel.sortByCategory(list = list), -1, UIVar.colorRed())
        }
        else if (date) {
            Button(onClick = {
                asc = !asc
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Ascend/Descend")
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
            if (asc)
            {
                ListXItemsTransactions(null, tViewModel.sortTransactionsByDate(asc = true, list = list), -1, UIVar.colorRed())
            }
            else ListXItemsTransactions(null,tViewModel.sortTransactionsByDate(list = list), -1, UIVar.colorRed())
        }
        else if (!search)
        {
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Log.d("bug1", "List size: ${list.size}")
            ListXItemsTransactions(null, list, -1, UIVar.colorRed(), reversed = true)
        }
    }

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
                Button(onClick = { navController.navigate("FinanceIncome") }, modifier = Modifier.weight(1f)) {
                    Text(text = "Jövedelmek ➤")
                }
                Spacer(modifier = Modifier.width(UIVar.Padding))
            }
        }
    }
}