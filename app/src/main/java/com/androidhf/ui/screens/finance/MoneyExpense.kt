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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.androidhf.data.Category
import com.androidhf.data.Data
import com.androidhf.data.Frequency
import com.androidhf.data.SavingsType
import com.androidhf.data.Transaction
import com.androidhf.ui.reuseable.NumberTextField
import com.androidhf.ui.reuseable.UIVar
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MoneyExpenseScreen(navController: NavController/*, viewModel: SavingsViewModel*/) {
    Data.topBarTitle = "Kiadás felvétel"
    var input by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(Frequency.EGYSZERI) }
    var category by remember { mutableStateOf(Category.ELOFIZETES) }

    var showPopup by remember { mutableStateOf(false) }

    fun onSubmit()
    {
        val amount = input.toIntOrNull()
        if (amount != null) {

            val transaction = Transaction(-amount, "TODO", LocalDate.now(), LocalTime.now(), category,frequency)

           Data.addTransaction(transaction/*, viewModel*/)
            if(transaction.frequency!=Frequency.EGYSZERI){
                transaction.isRepetitive=true
                Data.repetitiveTransactions.add(transaction)
            }


            navController.popBackStack() // visszalép az előző képernyőre
        }
    }

    if (showPopup) {
        Popup {
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 250.dp))
            {
                Box(
                    modifier = Modifier
                        .padding(40.dp)
                        .fillMaxWidth()
                        .border(4.dp, UIVar.boxBorderColor(), RoundedCornerShape(UIVar.Radius))
                        .background(MaterialTheme.colorScheme.onError, RoundedCornerShape(UIVar.Radius))
                        .padding(UIVar.Padding)
                        .align(Alignment.BottomCenter)
                ) {
                    Column {
                        Text("Ha véghez viszi ezt a tranzakciót, akkor túlcsordul az egyik Takaréka! Biztosan folytatja?")
                        Row {
                            Button(
                                onClick = {
                                    showPopup = false
                                    onSubmit()
                                },
                                modifier = Modifier.weight(3f)
                            ) {
                                Text("Igen")
                            }
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                            Button(onClick = {
                                showPopup = false
                            }, modifier = Modifier.weight(7f)
                            ) {
                                Text("Nem")
                            }
                        }

                    }

                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {





        FrequencyDropdownMenu(
            selected = frequency,
            onSelectedChange = { frequency = it }
        )

        CategoryDropdownMenu(
            selected = category,
            onSelectedChange = { category = it }
        )



        Text("Add meg az összeget:")

        NumberTextField(
            input = input,
            onInputChange = { input = it }
        )
        /*TextField(
            value = input,
            onValueChange = {
                if (it.matches(Regex("^\\d*\\.?\\d*\$"))) input = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

         */

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val amount = input.toIntOrNull()
            if (amount != null) {
                val found = Data.getSavingsList().filter {it.Type == SavingsType.EXPENSEGOAL_BYAMOUNT}.any{ item ->
                    item.Start - amount < item.Amount
                }
                if(found) showPopup = true
                else onSubmit()
            }

        })
        {
            Text("Hozzáadás és vissza")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("Mégse")
        }
    }
}



//Frequency ENUM-nak készült dropdown menu
@Composable
private fun FrequencyDropdownMenu(
    selected: Frequency,
    onSelectedChange: (Frequency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { expanded = true }) {
            Text(text = selected.displayName)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Frequency.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.displayName) },
                    onClick = {
                        onSelectedChange(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

//Category ENUM-nak készült dropdown menu
@Composable
private fun CategoryDropdownMenu(
    selected: Category,
    onSelectedChange: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { expanded = true }) {
            Text(text = selected.displayName)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val expenseCategory = Category.entries.filter { it.type== Category.Type.EXPENSE }

            expenseCategory.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.displayName) },
                    onClick = {
                        onSelectedChange(type)
                        expanded = false
                    }
                )
            }
        }
    }
}




