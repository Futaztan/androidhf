package com.androidhf.ui.screens.home

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidhf.data.enums.SavingsType
import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.ListXItemsTransactions
import com.androidhf.ui.reuseable.Panel
import com.androidhf.ui.reuseable.Report
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.finance.savingcards.SavingCard_Expense2
import com.androidhf.ui.screens.finance.savingcards.SavingCard_Income1
import com.androidhf.ui.screens.finance.savingcards.SavingCard_Income2
import com.androidhf.ui.screens.finance.viewmodel.RepetitiveTransactionViewModel
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel
import com.androidhf.ui.screens.login.auth.AuthService


@Composable
fun HomeScreen(transactionViewModel: TransactionViewModel,savingViewModel: SavingViewModel) {
    UIVar.topBarTitle = "Home"



    val money = transactionViewModel.balance.collectAsState().value

    val scrollState = rememberScrollState()
    val haptic = LocalView.current
    val context = LocalContext.current
    if(scrollState.value == scrollState.maxValue)     haptic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

    Column(
        modifier = Modifier.fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(UIVar.Padding),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Panel{
            if(!AuthService.isLoggedIn()) HeaderText("Szia Vendég")
            else HeaderText("Szia ${AuthService.getUserDisplayName()}")
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Row (modifier = Modifier.fillMaxWidth()){
            ListXItemsTransactions(transactionViewModel.incomeTransactions.collectAsState(), null,10,Color.Green,Modifier.weight(1f))
            Spacer(modifier = Modifier.width(UIVar.Padding))
            ListXItemsTransactions(transactionViewModel.expenseTransactions.collectAsState(), null,10,Color.Red,Modifier.weight(1f))
        }

        if(sViewModel.savings.collectAsState().value.takeLast(3).isNotEmpty())
        {
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Text("3 legújabb takarék")
        }
        sViewModel.savings.collectAsState().value.takeLast(3).forEach { item ->

            Spacer(modifier = Modifier.padding(UIVar.Padding))
            if(item.Type == SavingsType.INCOMEGOAL_BYAMOUNT)
            {
                SavingCard_Income2(item, { }, false)
            }
            else if(item.Type == SavingsType.INCOMEGOAL_BYTIME)
            {
                SavingCard_Income1(item, { }, false, transactionViewModel)
            }
            else
            {
                SavingCard_Expense2(item, { }, false)
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Report(transactionViewModel,savingViewModel)
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Spacer(modifier = Modifier.height(UIVar.Padding))

    }


}

