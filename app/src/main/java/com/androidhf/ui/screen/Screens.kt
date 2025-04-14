// com.androidhf.ui.screen.Screens.kt

package com.androidhf.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.androidhf.data.Datas

@Composable
fun HomeScreen() {
    Text("Home Screen", modifier = Modifier.fillMaxSize())

}

@Composable
fun PenzugyScreen() {
    val money = Datas.osszpenz
    var showPopup by remember { mutableStateOf(false) }
    var isMinus by remember { mutableStateOf(false) }
    Column ( modifier = Modifier.fillMaxSize())
    {
        Text("Pénzügy Screen")
        Button(onClick = {}) { Text( "$money") }
        Button(onClick = { showPopup =true; isMinus = false  }) { Text("penz hozzaad") }
        Button(onClick = { showPopup =true; isMinus = true }) { Text("penz elvesz") }
    }

    if(showPopup)
    {
        NumberInputPopup(
            onDismiss = { showPopup = false; isMinus=false },

            onConfirm = { amount->
                if(isMinus) moneyRemove(amount)
                else moneyAdd(amount)
            }
        )

    }


}

@Composable
fun NumberInputPopup(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var input by  remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adj meg egy számot") },
        text = {
            TextField(
                value = input,
                onValueChange = { newValue ->
                    // Csak számjegyeket és pontot enged be
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        input = newValue
                    }
                },
                placeholder = { Text("Pl. 123.45") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val number = input.toDoubleOrNull()
                    if (number != null) {
                        onConfirm(number)
                        onDismiss()
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}


fun moneyAdd(osszeg: Double) {

    Datas.osszpenz+= osszeg
}
fun moneyRemove(osszeg: Double) {

        Datas.osszpenz-= osszeg
}

@Composable
fun StockScreen() {
    Text("Stock Screen", modifier = Modifier.fillMaxSize())
}

@Composable
fun AIScreen() {
    Text("AI Screen", modifier = Modifier.fillMaxSize())
}
