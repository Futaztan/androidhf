package com.androidhf.ui.screens.finance

import android.app.DatePickerDialog
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.androidhf.ui.reuseable.Panel
import com.androidhf.ui.reuseable.UIVariables
import com.androidhf.ui.theme.AndroidhfTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.util.Calendar

@Composable
fun MoneySavingsScreen(navController: NavController, viewModel: SavingsViewModel)
{
    Data.topBarTitle = "Takarék felvétel"
    var input by remember { mutableStateOf("") }
    var input_invalid by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now().plusDays(14)) }
    var selectedType by remember { mutableStateOf(SavingsType.INCOMEGOAL_BYTIME) }
    var description by remember { mutableStateOf("") }
    var description_invalid by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var title_invalid by remember { mutableStateOf(false) }
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
    Column {
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
        Panel(centerItems = false, backgroundColor = input_background_color){
            Column{
                Text("Adja meg az összeget:", color = input_text_color)
                NumberTextField(
                    input = input,
                    onInputChange = { input = it },
                    placeholder = "50000"
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVariables.Padding))
        Panel(centerItems = false, backgroundColor = MaterialTheme.colorScheme.primaryContainer)
        {
            Column{
                Text("Válasszon egy dátumot:", color = MaterialTheme.colorScheme.onPrimaryContainer)
                Button(onClick = { datePickerDialog.show() }) {
                    Text(text = selectedDate.toString() ?: "Dátum kiválasztása")
                }
            }
        }
        Spacer(modifier = Modifier.height(UIVariables.Padding))
        Panel(centerItems = false, backgroundColor = MaterialTheme.colorScheme.primaryContainer) {
            Column{
                Text("Válasszon egy típust:", color = MaterialTheme.colorScheme.onPrimaryContainer)
                SimpleEnumDropdown(selected = selectedType, onSelectedChange = { selectedType = it })
            }
        }
        Spacer(modifier = Modifier.height(UIVariables.Padding))
        Panel(centerItems = false, backgroundColor = title_background_color)
        {
            Column{
                Text("Adjon egy nevet:", color = title_text_color)
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Cím") }
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVariables.Padding))
        Panel(centerItems = false, backgroundColor = desc_background_color) {
            Column {
                Text("Adjon egy rövid leírást miért hozta létre:", color = desc_text_color)
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Rövid leírás")}
                )
            }
        }
        Spacer(modifier = Modifier.height(UIVariables.Padding))
        Panel(backgroundColor = MaterialTheme.colorScheme.primaryContainer)
        {
            Row {
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
                            //showPopup = true
                        }
                    }
                ) {
                    Text("Mentés", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.width(UIVariables.Padding))
                Button(onClick = {navController.popBackStack()}) {
                    Text("Mégse", color = MaterialTheme.colorScheme.onPrimaryContainer)
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