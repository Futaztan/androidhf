package com.androidhf.ui.screens.finance

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androidhf.data.datatypes.Category
import com.androidhf.data.datatypes.Frequency
import com.androidhf.data.datatypes.RepetitiveTransaction
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoneyIncomeScreen(navController: NavController/*, viewModel: SavingsViewModel*/) {
    UIVar.topBarTitle = "Bevétel felvétel"

    val tViewModel: TransactionViewModel = hiltViewModel()
    val reptransViewModel : RepetitiveTransactionViewModel = hiltViewModel()
    val sViewModel: SavingViewModel = hiltViewModel()

    var input by remember { mutableStateOf("") }
    var input_invalid by remember { mutableStateOf(false) }
    var desc by remember { mutableStateOf("") }
    var desc_invalid by remember { mutableStateOf(false) }
    var frequency by remember { mutableStateOf(Frequency.EGYSZERI) }
    var category by remember { mutableStateOf(Category.FIZETES) }

    var onDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var fromDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var untilDate by remember { mutableStateOf<LocalDate>(LocalDate.now().plusMonths(3)) }
    var date_invalid by remember { mutableStateOf(false) }

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
        if (amount != null && amount > 0 && desc != "" && untilDate > fromDate && untilDate > LocalDate.now()) {
            val transaction = Transaction(
                amount,
                desc,
                onDate,
                LocalTime.now(),
                category,
                frequency
            )
            if (frequency == Frequency.EGYSZERI) {
                tViewModel.addTransaction(transaction)
                sViewModel.transactionAdded(amount)
            } else {
                val repetitiveTransaction =
                    RepetitiveTransaction(transaction,fromDate, untilDate)
                reptransViewModel.addRepTransaction(repetitiveTransaction)

            }

            navController.popBackStack() // visszalép az előző képernyőre
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



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UIVar.Padding),
        verticalArrangement = Arrangement.Center
    ) {


        Panel(centerItems = false) {
            Column {
                Text("Válassza ki a gyakoriságot:")
                FrequencyDropdownMenu(
                    selected = frequency,
                    onSelectedChange = { frequency = it }
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Panel(centerItems = false) {
            Column {
                Text("Válassza ki a kategóriát:")
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
                    Text("Melyik napon történt a tranzakció:", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Button(onClick = { onDatePickerDialog.show() }) {
                        Text(text = onDate.toString())
                    }
                }
            }
            else{
                Column {
                    Text("Melyik naptól kezdődjön", color = date_text_color)
                    Button(onClick = { fromDatePickerDialog.show() }) {
                        Text(text = fromDate.toString())
                    }
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                    Text("Meddig menjen:", color = date_text_color)
                    Button(onClick = { untilDatePickerDialog.show() }) {
                        Text(text = untilDate.toString())
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Panel(centerItems = false, backgroundColor = input_background_color) {
            Column {
                Text("Adja meg az összeget:", color = input_text_color)
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
                Text("Adja meg a rövid leírását:", color = desc_text_color)
                NumberTextField(
                    input = desc,
                    onInputChange = { desc = it },
                    placeholder = "XY Ösztöndíj",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))

        Panel {
            Row {
                Button(onClick = { onSubmit() })
                {
                    Text("Hozzáadás és vissza")
                }

                Spacer(modifier = Modifier.width(UIVar.Padding))

                Button(onClick = {
                    navController.popBackStack()
                }) {
                    Text("Mégse")
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


