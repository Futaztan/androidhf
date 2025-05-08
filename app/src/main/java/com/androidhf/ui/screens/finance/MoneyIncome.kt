package com.androidhf.ui.screens.finance

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidhf.data.Category
import com.androidhf.data.Data
import com.androidhf.data.Frequency
import com.androidhf.data.RepetitiveTransaction
import com.androidhf.data.Transaction
import com.androidhf.ui.reuseable.NumberTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoneyIncomeScreen(navController: NavController/*, viewModel: SavingsViewModel*/) {
    Data.topBarTitle = "Bevétel felvétel"
    var input by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(Frequency.EGYSZERI) }
    var category by remember { mutableStateOf(Category.FIZETES) }

    var onDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var fromDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var untilDate by remember { mutableStateOf<LocalDate>(LocalDate.now().plusMonths(3)) }
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, LocalDate.now().year)
        set(Calendar.MONTH, LocalDate.now().monthValue-1)
        set(Calendar.DAY_OF_MONTH, LocalDate.now().dayOfMonth)
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

    fun onSubmit() {
        val amount = input.toIntOrNull()
        if (amount != null) {
            val transaction = Transaction(
                amount,
                "TODO",
                onDate,
                LocalTime.now(),
                category,
                frequency
            )
            if (frequency == Frequency.EGYSZERI) {

                CoroutineScope(Dispatchers.IO).launch {
                    Data.addTransaction(transaction)
                }
            } else {
                val repetitiveTransaction =
                    RepetitiveTransaction(transaction,fromDate, untilDate)

                CoroutineScope(Dispatchers.IO).launch {
                    Data.addRepetitiveTransaction(repetitiveTransaction)
                }
            }

            navController.popBackStack() // visszalép az előző képernyőre
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



        Spacer(modifier = Modifier.height(8.dp))

        if(frequency==Frequency.EGYSZERI)
        {
            Text("Melyik napon történt a tranzakció:", color = MaterialTheme.colorScheme.onPrimaryContainer)
            Button(onClick = { onDatePickerDialog.show() }) {
                Text(text = onDate.toString())
            }
        }
        else{
            Text("Melyik naptól kezdődjön", color = MaterialTheme.colorScheme.onPrimaryContainer)
            Button(onClick = { fromDatePickerDialog.show() }) {
                Text(text = fromDate.toString())
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Meddig menjen:", color = MaterialTheme.colorScheme.onPrimaryContainer)
            Button(onClick = { untilDatePickerDialog.show() }) {
                Text(text = untilDate.toString())
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Add meg az összeget:")
        NumberTextField(
            input = input,
            onInputChange = { input = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSubmit() })
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
            val incomeCategory = Category.entries.filter { it.type == Category.Type.INCOME }

            incomeCategory.forEach { type ->
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


