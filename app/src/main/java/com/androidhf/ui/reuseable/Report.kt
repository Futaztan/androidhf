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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidhf.R
import androidx.room.util.getColumnIndex
import com.androidhf.data.enums.Category
import com.androidhf.data.enums.Frequency
import com.androidhf.ui.screens.finance.viewmodel.RepetitiveTransactionViewModel
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.first

@Composable
fun Report(transactionViewModel: TransactionViewModel, savingViewModel: SavingViewModel, rViewModel: RepetitiveTransactionViewModel)
{


    BorderBox() {
        Column {
            //1. sor
            Row {
                HeaderText(stringResource(id = R.string.report_balance))
                if(transactionViewModel.balance.collectAsState().value < 0) Text("${transactionViewModel.balance.collectAsState().value} Ft",
                    fontSize = UIVar.HeaderText,
                    fontWeight = FontWeight.Bold,
                    color = UIVar.colorRed())
                else if (transactionViewModel.balance.collectAsState().value > 0) Text("${transactionViewModel.balance.collectAsState().value} Ft",
                    fontSize = UIVar.HeaderText,
                    fontWeight = FontWeight.Bold,
                    color = UIVar.colorGreen())
                else Text("${transactionViewModel.balance.collectAsState().value} Ft",
                    fontSize = UIVar.HeaderText,
                    fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Text(stringResource(id = R.string.report_30day), fontSize = UIVar.HeaderText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UIVar.Padding))
            //2. sor 30 napos income és expense
            Column {
                Row {
                    BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                        Column {
                            Text(stringResource(id = R.string.report_income))
                            Text("${transactionViewModel.get30DaysIncome()} Ft", fontWeight = FontWeight.Bold, color = UIVar.colorGreen())
                        }
                    }
                    Spacer(modifier = Modifier.width(UIVar.Padding))
                    BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                        Column {
                            Text(stringResource(id = R.string.report_expense))
                            Text("${transactionViewModel.get30DaysExpense()} Ft", fontWeight = FontWeight.Bold, color = UIVar.colorRed())
                        }
                    }
                }
                if(transactionViewModel.get30DaysIncomeByType(LocalContext.current) != "" || transactionViewModel.get30DaysExpenseByType(LocalContext.current) != "")
                {
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                }
                Row {
                    if (transactionViewModel.get30DaysIncomeByType(LocalContext.current) != "")
                    {
                        BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                            Column {
                                Text(stringResource(id = R.string.report_mostincome))
                                Text(transactionViewModel.get30DaysIncomeByType(LocalContext.current), color = UIVar.colorGreen())
                            }
                        }
                    }
                    if (transactionViewModel.get30DaysIncomeByType(LocalContext.current) != "" && transactionViewModel.get30DaysExpenseByType(LocalContext.current) != "")
                    {
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                    if (transactionViewModel.get30DaysExpenseByType(LocalContext.current) != "")
                    {
                        BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                            Column {
                                Text(stringResource(id = R.string.report_expense))
                                Text(transactionViewModel.get30DaysExpenseByType(LocalContext.current), color = UIVar.colorRed())
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Text(stringResource(id = R.string.report_currsavings), fontSize = UIVar.HeaderText, fontWeight = FontWeight.Bold)
            //3. sor SAVINGEK
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Column {
                Panel(backgroundColor = UIVar.secondColor()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(id = R.string.report_savingnum))
                        Text(savingViewModel.savingsCount().toString())
                    }
                }
                if(savingViewModel.savingsCountByHold() != 0 || savingViewModel.savingsCountByLimit() != 0 || savingViewModel.savingsCountByCollect() != 0)
                {
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                }
                Row{
                    if (savingViewModel.savingsCountByCollect() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(id = R.string.report_collect))
                                Text(savingViewModel.savingsCountByCollect().toString())
                            }
                        }
                    }
                    if (savingViewModel.savingsCountByCollect() != 0 && (savingViewModel.savingsCountByLimit() != 0 || savingViewModel.savingsCountByHold() != 0))
                    {
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                    if (savingViewModel.savingsCountByLimit() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(id = R.string.report_limit))
                                Text(savingViewModel.savingsCountByLimit().toString())
                            }
                        }
                    }
                    if(savingViewModel.savingsCountByHold() != 0 && savingViewModel.savingsCountByLimit() != 0)
                    {
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                    if (savingViewModel.savingsCountByHold() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(id = R.string.report_hold))
                                Text(savingViewModel.savingsCountByHold().toString())
                            }
                        }
                    }
                }
                if (savingViewModel.savingsCountCompleted() != 0 || savingViewModel.savingsCountFailed() != 0)
                {
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                }
                Row {
                    if (savingViewModel.savingsCountCompleted() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(id = R.string.report_completed))
                                Text(savingViewModel.savingsCountCompleted().toString())
                            }
                        }
                    }
                    if (savingViewModel.savingsCountCompleted() != 0 && savingViewModel.savingsCountFailed() != 0)
                    {
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                    if (savingViewModel.savingsCountFailed() != 0)
                    {
                        Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(id = R.string.report_failed))
                                Text(savingViewModel.savingsCountFailed().toString())
                            }
                        }
                    }
                }
            }
            //4. sor REPETITIVE TRANSACTIONS
            val repetitivetransactions = rViewModel.repetitiveTransactions.collectAsState().value
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Text("Repetitive Transactions", fontSize = UIVar.HeaderText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Panel(backgroundColor = UIVar.secondColor()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Active Transactions:")
                    Text(repetitivetransactions.size.toString())
                }
            }
            rViewModel.repetitiveTransactions

            Spacer(modifier = Modifier.height(UIVar.Padding))
            Row {
                Panel(backgroundColor = UIVar.secondColor(), modifier = Modifier.weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Completed:")
                        Text(repetitivetransactions.size.toString())
                    }
                }
                if (repetitivetransactions.size > 0)
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
                                    repetitivetransactions.forEach{ item ->
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
                                    repetitivetransactions.forEach{ item ->
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
                                    repetitivetransactions.forEach{ item ->
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
                                    repetitivetransactions.forEach{ item ->
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
                                    repetitivetransactions.forEach{ item ->
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
                                    repetitivetransactions.forEach{ item ->
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