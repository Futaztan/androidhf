package com.androidhf.ui.screens.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidhf.data.Category
import com.androidhf.data.Data
import com.androidhf.data.Frequency
import com.androidhf.data.Transaction
import com.androidhf.ui.reuseable.NumberTextField
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MoneyExpenseScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(Frequency.EGYSZERI) }
    var category by remember { mutableStateOf(Category.ELOFIZETES) }

    fun onSubmit()
    {
        val amount = input.toIntOrNull()
        if (amount != null) {

            val transaction =
                Transaction(amount, "TODO", LocalDate.now(), LocalTime.now(), category,frequency)

            Data.expensesList.add(transaction)
            Data.addOsszpenz(-amount)
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




