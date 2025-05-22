package com.androidhf.ui.screens.finance.money

import android.app.DatePickerDialog
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.androidhf.R
import com.androidhf.data.enums.Category
import com.androidhf.data.enums.Frequency
import com.androidhf.data.datatypes.RepetitiveTransaction
import com.androidhf.data.enums.SavingsType
import com.androidhf.data.datatypes.Transaction
import com.androidhf.ui.reuseable.NumberTextField
import com.androidhf.ui.reuseable.Panel
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.finance.viewmodel.RepetitiveTransactionViewModel
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

@Composable
fun MoneyExpenseScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    reptransViewModel: RepetitiveTransactionViewModel,
    savingViewModel: SavingViewModel) {
    UIVar.topBarTitle = "Kiadás felvétel"



    var input by remember { mutableStateOf("") }
    var input_invalid by remember { mutableStateOf(false) }
    var desc by remember { mutableStateOf("") }
    var desc_invalid by remember { mutableStateOf(false) }
    var frequency by remember { mutableStateOf(Frequency.EGYSZERI) }
    var category by remember { mutableStateOf(Category.ELOFIZETES) }

    var showPopup by remember { mutableStateOf(false) }

    var onDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var fromDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var untilDate by remember { mutableStateOf<LocalDate>(LocalDate.now().plusMonths(3)) }
    var date_invalid by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, LocalDate.now().year)
        set(Calendar.MONTH, LocalDate.now().monthValue-1)
        set(Calendar.DAY_OF_MONTH, LocalDate.now().dayOfMonth)
    }

    val input_background_color: Color
    val input_text_color: Color
    val desc_background_color: Color
    val desc_text_color: Color
    val date_background_color: Color
    val date_text_color: Color

    if (input_invalid)
    {
        input_background_color = MaterialTheme.colorScheme.error
        input_text_color = MaterialTheme.colorScheme.onError
    }
    else
    {
        input_background_color = MaterialTheme.colorScheme.primaryContainer
        input_text_color = MaterialTheme.colorScheme.onPrimaryContainer
    }
    if (desc_invalid)
    {
        desc_background_color = MaterialTheme.colorScheme.error
        desc_text_color = MaterialTheme.colorScheme.onError
    }
    else
    {
        desc_background_color = MaterialTheme.colorScheme.primaryContainer
        desc_text_color = MaterialTheme.colorScheme.onPrimaryContainer
    }
    if (date_invalid)
    {
        date_background_color = MaterialTheme.colorScheme.error
        date_text_color = MaterialTheme.colorScheme.onError
    }
    else
    {
        date_background_color = MaterialTheme.colorScheme.primaryContainer
        date_text_color = MaterialTheme.colorScheme.onPrimaryContainer
    }

    val context = LocalContext.current
    val onDatePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    val fromDatePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fromDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    val untilDatePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                untilDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun onConfirm()
    {
        val amount = input.toInt()
        val transaction = Transaction(
            -amount,
            desc,
            onDate,
            LocalTime.now(),
            category,
            frequency
        )
        if (frequency == Frequency.EGYSZERI) {
            transactionViewModel.addTransaction(transaction)
            savingViewModel.transactionAdded(-amount)
        } else {
            val repetitiveTransaction =
                RepetitiveTransaction(transaction,fromDate, untilDate)

            //TODO addRepetitiveTransaction(repetitiveTransaction)
        }
        navController.popBackStack()
    }

    val savings = savingViewModel.savings.collectAsState()
    fun onSubmit() {
        val amount = input.toIntOrNull()
        if (amount != null && amount > 0 && desc != "" && untilDate > fromDate && untilDate > LocalDate.now()) {
            val found = savings.value.filter {it.Type == SavingsType.EXPENSEGOAL_BYAMOUNT}.any{ item ->
                !item.Closed && item.Start - amount <= item.Amount
            }
            if(found) showPopup = true
            else onConfirm()
        }
        else{
            if(input.isBlank() || input.toInt() < 0)
            {
                input_invalid = true
            }
            else input_invalid = false
            if(desc.isBlank())
            {
                desc_invalid = true
            }
            else desc_invalid = false
            if(untilDate <= fromDate || untilDate <= LocalDate.now())
            {
                date_invalid = true
            }
            else date_invalid = false
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
                        Text(stringResource(id = R.string.moneyexpense_overflow))
                        Row {
                            Button(
                                onClick = {
                                    showPopup = false
                                    onConfirm()
                                },
                                modifier = Modifier.weight(3f)
                            ) {
                                Text(stringResource(id = R.string.general_yes))
                            }
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                            Button(onClick = {
                                showPopup = false
                            }, modifier = Modifier.weight(7f)
                            ) {
                                Text(stringResource(id = R.string.general_no))
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
            .padding(UIVar.Padding),
        verticalArrangement = Arrangement.Center
    ) {




        Panel(centerItems = false) {
            Column {
                Text(stringResource(id = R.string.moneyexpense_frequency))
                FrequencyDropdownMenu(
                    selected = frequency,
                    onSelectedChange = { frequency = it }
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Panel(centerItems = false) {
            Column {
                Text(stringResource(id = R.string.moneyexpense_category))
                CategoryDropdownMenu(
                    selected = category,
                    onSelectedChange = { category = it }
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))

        Panel(centerItems = false, backgroundColor = date_background_color)
        {
            if(frequency== Frequency.EGYSZERI)
            {
                Column {
                    Text(stringResource(id = R.string.moneyincome_whichday), color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Button(onClick = { onDatePickerDialog.show() }) {
                        Text(text = onDate.toString())
                    }
                }
            }
            else{
                Column {
                    Text(stringResource(id = R.string.moneyincome_whichday), color = date_text_color)
                    Button(onClick = { fromDatePickerDialog.show() }) {
                        Text(text = fromDate.toString())
                    }
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                    Text(stringResource(id = R.string.moneyincome_howlong), color = date_text_color)
                    Button(onClick = { untilDatePickerDialog.show() }) {
                        Text(text = untilDate.toString())
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))

        Panel(centerItems = false, backgroundColor = input_background_color) {
            Column {
                Text(stringResource(id = R.string.moneysavings_enteramount), color = input_text_color)
                NumberTextField(
                    input = input,
                    onInputChange = { input = it },
                    placeholder = "10000",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Panel(centerItems = false, backgroundColor = desc_background_color) {
            Column {
                Text(stringResource(id = R.string.moneysavings_shortdescdialog), color = desc_text_color)
                TextField(
                    value = desc,
                    onValueChange = { desc = it },
                    placeholder = { Text(stringResource(id = R.string.moneyexpense_weeklyshop)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))

        Panel {
            Row {
                Button(onClick = {
                    onSubmit()
                })
                {
                    Text(stringResource(id = R.string.moneyincome_submit))
                }
                Spacer(modifier = Modifier.width(UIVar.Padding))
                Button(onClick = {
                    navController.popBackStack()
                }) {
                    Text(stringResource(id = R.string.general_cancel))
                }
            }
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
            Text(text = selected.getDisplayName(LocalContext.current))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Frequency.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.getDisplayName(LocalContext.current)) },
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
            Text(text = selected.getDisplayName(LocalContext.current))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val expenseCategory = Category.entries.filter { it.type== Category.Type.EXPENSE }

            expenseCategory.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.getDisplayName(LocalContext.current)) },
                    onClick = {
                        onSelectedChange(type)
                        expanded = false
                    }
                )
            }
        }
    }
}




