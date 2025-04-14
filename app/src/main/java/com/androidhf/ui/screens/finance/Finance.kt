package com.androidhf.ui.screens.finance

import androidx.compose.foundation.layout.Column
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
import androidx.navigation.NavHostController
import com.androidhf.data.Datas

@Composable
fun FinanceScreen(navHostController: NavHostController) {
    val money = Datas.osszpenz

    var isMinus by remember { mutableStateOf(false) }
    Column ( modifier = Modifier.fillMaxSize())
    {
        Text("Pénzügy Screen")
        Button(onClick = {}) { Text( "$money") }

        Button(onClick = {navHostController.navigate("money_income")}) { Text("bevetel") }
        Button(onClick = {navHostController.navigate("money_expense")}) { Text("kiadas") }
    }




}




fun changeMoney(osszeg: Double) {

    Datas.osszpenz+= osszeg
}
fun moneyRemove(osszeg: Double) {

    Datas.osszpenz-= osszeg
}