package com.androidhf.ui.screens.home

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import com.androidhf.data.Data
import com.androidhf.data.SavingsType
import com.androidhf.ui.reuseable.FirstXItemsTransactions
import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.Panel
import com.androidhf.ui.reuseable.UIVar

import com.androidhf.ui.screens.finance.savingcards.SavingCard_Expense2
import com.androidhf.ui.screens.finance.savingcards.SavingCard_Income1
import com.androidhf.ui.screens.finance.savingcards.SavingCard_Income2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    Data.topBarTitle = "Home"

    val scrollState = rememberScrollState()
    val haptic = LocalView.current

    if(scrollState.value == scrollState.maxValue)     haptic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

    Column(
        modifier = Modifier.fillMaxWidth()
                            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Panel{
            HeaderText("Szia Teszt!")
        }

        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("${Data.osszpenz}") }
        Row (modifier = Modifier.fillMaxWidth()){
            FirstXItemsTransactions(Data.getIncomesList(),10,Color.Green,Modifier.weight(1f))
            Spacer(modifier = Modifier.width(UIVar.Padding))
            FirstXItemsTransactions(Data.getExpensesList(),10,Color.Red,Modifier.weight(1f))
        }
        Text("3 legújabb takarék")
        Data.getSavingsList().takeLast(3).forEach { item ->
            Spacer(modifier = Modifier.padding(UIVar.Padding))
            if(item.Type == SavingsType.INCOMEGOAL_BYAMOUNT)
            {
                SavingCard_Income2(item, { }, false)
            }
            else if(item.Type == SavingsType.INCOMEGOAL_BYTIME)
            {
                SavingCard_Income1(item, { }, false)
            }
            else
            {
                SavingCard_Expense2(item, { }, false)
            }
        }
        Button(onClick = {}) { Text("Stock market:") } //TODO
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                Data.saveTransactions()

            }
        }) { Text("SAVE") }
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                Data.loadTransactions()
                Data.loadSaves()
            }

        }) { Text("LOAD")}
    }
}

