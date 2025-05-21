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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androidhf.data.datatypes.Savings
import com.androidhf.data.enums.SavingsType
import com.androidhf.ui.reuseable.NumberTextField
import com.androidhf.ui.reuseable.Panel
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.finance.viewmodel.RepetitiveTransactionViewModel
import com.androidhf.ui.screens.finance.viewmodel.SavingViewModel
import com.androidhf.ui.screens.finance.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.util.Calendar

@Composable
fun MoneySavingsScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    savingViewModel: SavingViewModel)
{
    UIVar.topBarTitle = "Takarék felvétel"



    val osszeg = transactionViewModel.balance.collectAsState().value

    var input by remember { mutableStateOf("") }
    var input_invalid by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now().plusDays(14)) }
    var selectedType by remember { mutableStateOf(SavingsType.INCOMEGOAL_BYTIME) }
    var description by remember { mutableStateOf("") }
    var description_invalid by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var title_invalid by remember { mutableStateOf(false) }
    var date_invalid by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }

    if (showPopup) {
        LaunchedEffect(Unit) {
            delay(2000)
            showPopup = false
        }
        Popup {
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 300.dp))
            {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .border(1.dp, Color.Black)
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text("Hiányos a kitöltés!", color = Color.Black)
                }
            }
        }
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, selectedDate.year)
        set(Calendar.MONTH, selectedDate.monthValue-1)
        set(Calendar.DAY_OF_MONTH, selectedDate.dayOfMonth)
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    var input_background_color: Color
    var input_text_color: Color
    var desc_background_color: Color
    var desc_text_color: Color
    var title_background_color: Color
    var title_text_color: Color
    var date_background_color: Color
    var date_text_color: Color
    Column(modifier = Modifier.padding(UIVar.Padding).fillMaxSize(), verticalArrangement = Arrangement.Center) {
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
        if (description_invalid)
        {
            desc_background_color = MaterialTheme.colorScheme.error
            desc_text_color = MaterialTheme.colorScheme.onError
        }
        else
        {
            desc_background_color = MaterialTheme.colorScheme.primaryContainer
            desc_text_color = MaterialTheme.colorScheme.onPrimaryContainer
        }
        if (title_invalid)
        {
            title_background_color = MaterialTheme.colorScheme.error
            title_text_color = MaterialTheme.colorScheme.onError
        }
        else
        {
            title_background_color = MaterialTheme.colorScheme.primaryContainer
            title_text_color = MaterialTheme.colorScheme.onPrimaryContainer
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
        Panel(centerItems = false, backgroundColor = input_background_color){
            Column{
                Text("Adja meg az összeget:", color = input_text_color)
                NumberTextField(
                    input = input,
                    onInputChange = { input = it },
                    placeholder = "50000",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Panel(centerItems = false, backgroundColor = date_background_color)
        {
            Column{
                Text("Válasszon egy dátumot:", color = date_text_color)
                Button(onClick = { datePickerDialog.show() }) {
                    Text(text = selectedDate.toString() ?: "Dátum kiválasztása")
                }
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Panel(centerItems = false, backgroundColor = MaterialTheme.colorScheme.primaryContainer) {
            Column{
                Text("Válasszon egy típust:", color = MaterialTheme.colorScheme.onPrimaryContainer)
                SimpleEnumDropdown(selected = selectedType, onSelectedChange = { selectedType = it })
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Panel(centerItems = false, backgroundColor = title_background_color)
        {
            Column{
                Text("Adjon egy nevet:", color = title_text_color)
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Cím") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Panel(centerItems = false, backgroundColor = desc_background_color) {
            Column {
                Text("Adjon egy rövid leírást miért hozta létre:", color = desc_text_color)
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Rövid leírás")},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVar.Padding))
        Panel(backgroundColor = MaterialTheme.colorScheme.primaryContainer)
        {
            Row {
                Button(
                    onClick = {
                        if (input.isNotBlank() && description.isNotBlank() && title.isNotBlank() && input.toInt() > 0 && selectedDate >= LocalDate.now()) {
                            selectedDate.let { date ->

                                var saving: Savings
                                if(selectedType == SavingsType.INCOMEGOAL_BYAMOUNT)
                                {
                                     saving = Savings(
                                        input.toInt(),
                                        LocalDate.now(),
                                        date,
                                        selectedType,
                                        title,
                                        description,
                                        0
                                    )
                                }
                                else if(selectedType == SavingsType.EXPENSEGOAL_BYAMOUNT)
                                {
                                    saving = Savings(
                                        input.toInt()*-1,
                                        LocalDate.now(),
                                        date,
                                        selectedType,
                                        title,
                                        description,
                                        0
                                    )
                                }
                                else
                                {
                                    saving = Savings(
                                        input.toInt(),
                                        LocalDate.now(),
                                        date,
                                        selectedType,
                                        title,
                                        description,
                                        osszeg
                                    )
                                }
                                savingViewModel.addSaving(saving)
                                navController.popBackStack()
                            }
                        }
                        else{
                            if(input.isBlank() || input.toInt() < 0)
                            {
                                input_invalid = true
                            }
                            else input_invalid = false
                            if(description.isBlank())
                            {
                                description_invalid = true
                            }
                            else description_invalid = false
                            if(title.isBlank())
                            {
                                title_invalid = true
                            }
                            else title_invalid = false

                            if(selectedDate < LocalDate.now())
                            {
                                date_invalid = true
                            }
                            else date_invalid = false
                            //showPopup = true
                        }
                    }
                ) {
                    Text("Mentés")
                }
                Spacer(modifier = Modifier.width(UIVar.Padding))
                Button(onClick = {navController.popBackStack()}) {
                    Text("Mégse")
                }
            }
        }


    }
}

@Composable
fun SimpleEnumDropdown(
    selected: SavingsType,
    onSelectedChange: (SavingsType) -> Unit
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
            SavingsType.entries.forEach { type ->
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