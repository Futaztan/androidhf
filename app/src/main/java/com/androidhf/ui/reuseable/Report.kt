package com.androidhf.ui.reuseable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel

@Composable
fun Report(transactionViewModel: TransactionViewModel, savingViewModel: SavingViewModel)
{



    BorderBox() {
        Column {
            //1. sor
            Row {
                HeaderText("Egyenleg: ")
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
            Text("30 Day Statistics", fontSize = UIVar.HeaderText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UIVar.Padding))
            //2. sor 30 napos income és expense
            Column {
                Row {
                    BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                        Column {
                            Text("Income:")
                            Text("${transactionViewModel.get30DaysIncome()} Ft", fontWeight = FontWeight.Bold, color = UIVar.colorGreen())
                        }
                    }
                    Spacer(modifier = Modifier.width(UIVar.Padding))
                    BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                        Column {
                            Text("Expense:")
                            Text("${transactionViewModel.get30DaysExpense()} Ft", fontWeight = FontWeight.Bold, color = UIVar.colorRed())
                        }
                    }
                }
                if(transactionViewModel.get30DaysIncomeByType() != "" || transactionViewModel.get30DaysExpenseByType() != "")
                {
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                }
                Row {
                    if (transactionViewModel.get30DaysIncomeByType() != "")
                    {
                        BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                            Column {
                                Text("Most income from:")
                                Text(transactionViewModel.get30DaysIncomeByType(), color = UIVar.colorGreen())
                            }
                        }
                    }
                    if (transactionViewModel.get30DaysIncomeByType() != "" && transactionViewModel.get30DaysExpenseByType() != "")
                    {
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                    if (transactionViewModel.get30DaysExpenseByType() != "")
                    {
                        BorderBox(modifier = Modifier.weight(1f), backgroundColor = UIVar.secondColor(), borderSize = 2.dp) {
                            Column {
                                Text("Most expense from:")
                                Text(transactionViewModel.get30DaysExpenseByType(), color = UIVar.colorRed())
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Text("All Current Savings", fontSize = UIVar.HeaderText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UIVar.Padding))
            Column {
                Panel(backgroundColor = UIVar.secondColor()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Number of Savings:")
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
                                Text("Collect:")
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
                                Text("Limit:")
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
                                Text("Hold:")
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
                                Text("Completed:")
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
                                Text("Failed:")
                                Text(savingViewModel.savingsCountFailed().toString())
                            }
                        }
                    }
                }
            }
        }

    }
}