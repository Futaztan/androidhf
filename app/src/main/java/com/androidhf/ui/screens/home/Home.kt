package com.androidhf.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidhf.data.Datas
import com.androidhf.ui.reuseable.HeaderText

@Preview
@Composable
fun HomeScreen() {
    listafeltoles()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally)

    {
        HeaderText("Szia Teszt!")
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("${Datas.osszpenz}") }
        Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
            FirstXItemsList(Datas.incomesList,10,Color.Green,Modifier.weight(1f))
            FirstXItemsList(Datas.expensesList,10,Color.Red,Modifier.weight(1f))
        }



        Button(onClick = {}) { Text("Stock market:") }
    }




}
fun listafeltoles()
{
    for (i in 1..20)
    {
        Datas.incomesList.add((i*50).toDouble())
        Datas.expensesList.add((i*50).toDouble())
    }
}

@Composable
fun FirstXItemsList(items: SnapshotStateList<Double>, count: Int, _color : Color, _modifier : Modifier) {
    val firstItems = items.take(count)

    Column(modifier = _modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        firstItems.forEach { item ->
            Text(text = "$item", modifier = Modifier.padding(8.dp), color = _color)
        }
    }
}

