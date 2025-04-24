package com.androidhf.ui.screens.finance

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import com.androidhf.data.Data
import com.androidhf.data.Savings
import com.androidhf.data.SavingsType
import com.androidhf.ui.reuseable.NumberTextField
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.util.Calendar

@Composable
fun MoneySavingsScreen(navController: NavController, viewModel: SavingsViewModel)
{
    Data.topBarTitle = "Takarék felvétel"
    var input by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now().plusDays(14)) }
    var selectedType by remember { mutableStateOf(SavingsType.INCOMEGOAL_BYTIME) }
    var description by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
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

    Column {
        Text("Adja meg az összeget:")
        NumberTextField(
            input = input,
            onInputChange = { input = it },
            placeholder = "50000"
        )
        Text("Válasszon egy dátumot:")
        Button(onClick = { datePickerDialog.show() }) {
            Text(text = selectedDate.toString() ?: "Dátum kiválasztása")
        }
        Text("Válasszon egy típust:")
        SimpleEnumDropdown(selected = selectedType, onSelectedChange = { selectedType = it })
        Text("Adjon egy nevet:")
        TextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Cím") }
        )
        Text("Adjon egy rövid leírást miért hozta létre:")
        TextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Rövid leírás")}
        )
        Button(
            onClick = {
                if (input.isNotBlank() && description.isNotBlank() && title.isNotBlank() && input.toInt() > 0) {
                    selectedDate.let { date ->
                        val saving = Savings(
                            input.toInt(),
                            LocalDate.now(),
                            date,
                            selectedType,
                            title,
                            description,
                            Data.osszpenz
                        )
                        viewModel.addSaving(saving)
                        navController.popBackStack()
                    }
                }
                else{
                    showPopup = true
                }
            }
        ) {
            Text("Mentés")
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