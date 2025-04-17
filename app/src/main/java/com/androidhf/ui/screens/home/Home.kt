package com.androidhf.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidhf.data.Category
import com.androidhf.data.Data
import com.androidhf.data.Transaction
import com.androidhf.ui.reuseable.FirstXItemsList
import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.Panel
import com.androidhf.ui.reuseable.UIVariables
import java.time.LocalDate
import java.time.LocalTime

@Preview
@Composable
fun HomeScreen() {
    listafeltoles()
    Column(
        modifier = Modifier.fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally)

    {
        Panel{
            HeaderText("Szia Teszt!")
        }

        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("${Data.osszpenz}") }
        Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
            FirstXItemsList(Data.incomesList,10,Color.Green,Modifier.weight(1f))
            Spacer(modifier = Modifier.width(UIVariables.Padding))
            FirstXItemsList(Data.expensesList,10,Color.Red,Modifier.weight(1f))
        }



        Button(onClick = {}) { Text("Stock market:") }
    }




}
fun listafeltoles()
{
    for (i in 1..20)
    {
        val transactionplus = Transaction(i*50.0,"TESZT$i", LocalDate.now(), LocalTime.now(), Category.FIZETES)
        val transactionminus = Transaction(i*50.0,"TESZT$i", LocalDate.now(), LocalTime.now(), Category.ELOFIZETES)
        Data.incomesList.add(transactionplus)
        Data.expensesList.add(transactionminus)
    }
}

