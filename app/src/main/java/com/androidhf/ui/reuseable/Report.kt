package com.androidhf.ui.reuseable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.util.getColumnIndex
import com.androidhf.data.enums.Category
import com.androidhf.data.enums.Frequency
import com.androidhf.ui.screens.finance.viewmodel.RepetitiveTransactionViewModel
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel

@Composable
fun Report()
{
    val sViewModel: SavingViewModel = hiltViewModel()
    val tViewModel: TransactionViewModel = hiltViewModel()
    val rViewModel: RepetitiveTransactionViewModel = hiltViewModel()

    BorderBox() {
        Column {
            //1. sor
            Row {
                HeaderText("Egyenleg: ")
                if(tViewModel.balance.collectAsState().value < 0) Text("${tViewModel.balance.collectAsState().value} Ft",
                    fontSize = UIVar.HeaderText,
                    fontWeight = FontWeight.Bold,
                    color = UIVar.colorRed())
                else if (tViewModel.balance.collectAsState().value > 0) Text("${tViewModel.balance.collectAsState().value} Ft",
                    fontSize = UIVar.HeaderText,
                    fontWeight = FontWeight.Bold,
                    color = UIVar.colorGreen())
                else Text("${tViewModel.balance.collectAsState().value} Ft",
                    fontSize = UIVar.HeaderText,
                    fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Text("30 Day Statistics", fontSize = UIVar.HeaderText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UIVar.Padding))
            //2. sor 30 napos income és expense
            Column {
                Row {
                    BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                        Column {
                            Text("Income:")
                            Text("${tViewModel.get30DaysIncome()} Ft", fontWeight = FontWeight.Bold, color = UIVar.colorGreen())
                        }
                    }
                    Spacer(modifier = Modifier.width(UIVar.Padding))
                    BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                        Column {
                            Text("Expense:")
                            Text("${tViewModel.get30DaysExpense()} Ft", fontWeight = FontWeight.Bold, color = UIVar.colorRed())
                        }
                    }
                }
                if(tViewModel.get30DaysIncomeByType() != "" || tViewModel.get30DaysExpenseByType() != "")
                {
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                }
                Row {
                    if (tViewModel.get30DaysIncomeByType() != "")
                    {
                        BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                            Column {
                                Text("Most income from:")
                                Text(tViewModel.get30DaysIncomeByType(), color = UIVar.colorGreen())
                            }
                        }
                    }
                    if (tViewModel.get30DaysIncomeByType() != "" && tViewModel.get30DaysExpenseByType() != "")
                    {
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                    if (tViewModel.get30DaysExpenseByType() != "")
                    {
                        BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                            Column {
                                Text("Most expense from:")
                                Text(tViewModel.get30DaysExpenseByType(), color = UIVar.colorRed())
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
            //3. sor SAVINGEK
            Text("All Current Savings", fontSize = UIVar.HeaderText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Column {
                Panel(backgroundColor = UIVar.secondColor()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Number of Savings:")
                        Text(sViewModel.savingsCount().toString())
                    }
                }
                if(sViewModel.savingsCountByHold() != 0 || sViewModel.savingsCountByLimit() != 0 || sViewModel.savingsCountByCollect() != 0)
                {
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                }
                Row{
                    if (sViewModel.savingsCountByCollect() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Collect:")
                                Text(sViewModel.savingsCountByCollect().toString())
                            }
                        }
                    }
                    if (sViewModel.savingsCountByCollect() != 0 && (sViewModel.savingsCountByLimit() != 0 || sViewModel.savingsCountByHold() != 0))
                    {
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                    if (sViewModel.savingsCountByLimit() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Limit:")
                                Text(sViewModel.savingsCountByLimit().toString())
                            }
                        }
                    }
                    if(sViewModel.savingsCountByHold() != 0 && sViewModel.savingsCountByLimit() != 0)
                    {
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                    if (sViewModel.savingsCountByHold() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Hold:")
                                Text(sViewModel.savingsCountByHold().toString())
                            }
                        }
                    }
                }
                if (sViewModel.savingsCountCompleted() != 0 || sViewModel.savingsCountFailed() != 0)
                {
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                }
                Row {
                    if (sViewModel.savingsCountCompleted() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Completed:")
                                Text(sViewModel.savingsCountCompleted().toString())
                            }
                        }
                    }
                    if (sViewModel.savingsCountCompleted() != 0 && sViewModel.savingsCountFailed() != 0)
                    {
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                    if (sViewModel.savingsCountFailed() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Failed:")
                                Text(sViewModel.savingsCountFailed().toString())
                            }
                        }
                    }
                }
            }
            //4. sor REPETITIVE TRANSACTIONS
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Text("Repetitive Transactions", fontSize = UIVar.HeaderText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Panel(backgroundColor = UIVar.secondColor()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Active Transactions:")
                    Text(rViewModel.repetitiveTransactionList.size.toString())
                }
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Row {
                Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Completed:")
                        Text(rViewModel.repetitiveTransactionList.size.toString())
                    }
                }
                if (rViewModel.repetitiveTransactionList.size > 0)
                {

                    Spacer(modifier = Modifier.height(UIVar.Padding))
                    Column()
                    {
                        Text("Daily:")
                        Row {
                            Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    var napiIncome: Int = 0
                                    Text("Income:")
                                    rViewModel.repetitiveTransactionList.first().forEach{ item ->
                                        if (item.transaction.category.type == Category.Type.INCOME && item.transaction.frequency == Frequency.NAPI)
                                        {
                                            napiIncome += item.transaction.amount
                                        }
                                    }
                                    Text(napiIncome.toString() + " Ft", color = UIVar.colorGreen())
                                }
                            }
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                            Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    var napiExpense: Int = 0
                                    Text("Expense:")
                                    rViewModel.repetitiveTransactionList.first().forEach{ item ->
                                        if (item.transaction.category.type == Category.Type.EXPENSE && item.transaction.frequency == Frequency.NAPI)
                                        {
                                            napiExpense += item.transaction.amount
                                        }
                                    }
                                    Text(napiExpense.toString() + " Ft", color = UIVar.colorRed())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(UIVar.Padding))
                        Text("Weekly:")
                        Row {
                            Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    var hetiIncome: Int = 0
                                    Text("Income:")
                                    rViewModel.repetitiveTransactionList.first().forEach{ item ->
                                        if (item.transaction.category.type == Category.Type.INCOME && item.transaction.frequency == Frequency.HETI)
                                        {
                                            hetiIncome += item.transaction.amount
                                        }
                                    }
                                    Text(hetiIncome.toString() + " Ft", color = UIVar.colorGreen())
                                }
                            }
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                            Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    var hetiExpense: Int = 0
                                    Text("Expense:")
                                    rViewModel.repetitiveTransactionList.first().forEach{ item ->
                                        if (item.transaction.category.type == Category.Type.EXPENSE && item.transaction.frequency == Frequency.HETI)
                                        {
                                            hetiExpense += item.transaction.amount
                                        }
                                    }
                                    Text(hetiExpense.toString() + " Ft", color = UIVar.colorRed())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(UIVar.Padding))
                        Text("Monthly:")
                        Row {
                            Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    var haviIncome: Int = 0
                                    Text("Income:")
                                    rViewModel.repetitiveTransactionList.first().forEach{ item ->
                                        if (item.transaction.category.type == Category.Type.INCOME && item.transaction.frequency == Frequency.HAVI)
                                        {
                                            haviIncome += item.transaction.amount
                                        }
                                    }
                                    Text(haviIncome.toString() + " Ft", color = UIVar.colorGreen())
                                }
                            }
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                            Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    var haviExpense: Int = 0
                                    Text("Expense:")
                                    rViewModel.repetitiveTransactionList.first().forEach{ item ->
                                        if (item.transaction.category.type == Category.Type.EXPENSE && item.transaction.frequency == Frequency.HAVI)
                                        {
                                            haviExpense += item.transaction.amount
                                        }
                                    }
                                    Text(haviExpense.toString() + " Ft", color = UIVar.colorRed())
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}