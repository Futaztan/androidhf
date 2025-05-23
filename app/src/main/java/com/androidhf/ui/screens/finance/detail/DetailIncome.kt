package com.androidhf.ui.screens.finance.detail

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.androidhf.R
import com.androidhf.data.datatypes.Transaction
import com.androidhf.ui.reuseable.ListXItemsTransactions
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel

@Composable
fun FinanceIncome(navController: NavHostController, transactionViewModel: TransactionViewModel) {
    UIVar.topBarTitle = stringResource(id = R.string.financeincome_title)


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

    val transactions = transactionViewModel.incomeTransactions.collectAsState()
    val currentTransactions = transactions.value

    val context = LocalContext.current
    val list: List<Transaction> = remember(input, currentTransactions) {
        if (input.isEmpty()) {
            currentTransactions
        } else {
            val filteredList = transactionViewModel.incomeContainsList(input,context)
            filteredList
        }
    }

    Column (modifier = Modifier.padding(UIVar.Padding).verticalScroll(rememberScrollState())) {
        Text(text = stringResource(id = R.string.financeincome_sortby), modifier = Modifier.padding(bottom = UIVar.Padding), fontSize = UIVar.HeaderText)
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            Button(onClick = {
                amount = !amount
                category = false
                date = false
            }, colors = buttonAmountColor) {
                Text(text = stringResource(id = R.string.financeincome_amount))
            }
            Spacer(modifier = Modifier.padding(UIVar.Padding))
            Button(onClick = {
                category = !category
                amount = false
                date = false
            }, colors = buttonCatColor) {
                Text(text = stringResource(id = R.string.financeincome_category))
            }
            Spacer(modifier = Modifier.padding(UIVar.Padding))
            Button(onClick = {
                date = !date
                amount = false
                category = false
            }, colors = buttonDateColor) {
                Text(text = stringResource(id = R.string.financeincome_date))
            }
            Spacer(modifier = Modifier.padding(UIVar.Padding))
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
                Text(text = stringResource(id = R.string.stock_search2))
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
                placeholder = { Text(stringResource(id = R.string.stock_search2)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(UIVar.Padding))
            if (!date && !category && !amount && list.isNotEmpty())
            {
                ListXItemsTransactions(null, list, -1, UIVar.colorGreen(), _fitMaxWidth = true)
            }
        }
        if (list.isNotEmpty())
        {
            if (amount) {
                Button(onClick = {
                    asc = !asc
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.financeincome_ascdesc))
                }
                Spacer(modifier = Modifier.height(UIVar.Padding))
                if (asc)
                {
                    ListXItemsTransactions(null, transactionViewModel.sortTransactionsByAmount(list = list), -1, UIVar.colorGreen(), _fitMaxWidth = true)
                }
                else ListXItemsTransactions(null,transactionViewModel.sortTransactionsByAmount(list = list), -1, UIVar.colorGreen(), reversed = true, _fitMaxWidth = true)
            }
            else if (category) {
                Spacer(modifier = Modifier.height(UIVar.Padding))
                ListXItemsTransactions(null, transactionViewModel.sortByCategory(list = list, context), -1, UIVar.colorGreen(), _fitMaxWidth = true)
            }
            else if (date) {
                Button(onClick = {
                    asc = !asc
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.financeincome_ascdesc))
                }
                Spacer(modifier = Modifier.height(UIVar.Padding))
                if (asc)
                {
                    ListXItemsTransactions(null, transactionViewModel.sortTransactionsByDate(asc = true, list = list), -1, UIVar.colorGreen(), _fitMaxWidth = true)
                }
                else ListXItemsTransactions(null,transactionViewModel.sortTransactionsByDate(list = list), -1, UIVar.colorGreen(), _fitMaxWidth = true)
            }
            else if (!search)
            {
                Spacer(modifier = Modifier.height(UIVar.Padding))
                Log.d("bug1", "List size: ${list.size}")
                ListXItemsTransactions(null, list, -1, UIVar.colorGreen(), reversed = true, _fitMaxWidth = true)
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
        }
    }

    Box(modifier = Modifier.fillMaxSize())
    {
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth())
        {
            Row(modifier = Modifier, Arrangement.SpaceAround) {
                Spacer(modifier = Modifier.width(UIVar.Padding))
                Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.general_back))
                }
                Spacer(modifier = Modifier.width(UIVar.Padding))
                Button(onClick = { navController.navigate("FinanceExpense") }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.financeincome_toexpenses))
                }
                Spacer(modifier = Modifier.width(UIVar.Padding))
            }
        }
    }
}