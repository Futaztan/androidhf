package com.androidhf.ui.screens.finance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.androidhf.data.Data
import com.androidhf.ui.reuseable.BorderBox
import com.androidhf.ui.reuseable.FirstXItemsList
import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.LastXItemsList
import com.androidhf.ui.reuseable.UIVariables


@Composable
fun FinanceScreen(navHostController: NavHostController) {
    Box(modifier = Modifier
        .padding(UIVariables.Padding)
        .fillMaxSize()
    )
    {
        Column( modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
            BorderBox() {Finance_ui_egyenleg(navHostController)}
            Spacer(modifier = Modifier.height(UIVariables.Padding))
            Row(modifier = Modifier.fillMaxWidth()) {
                BorderBox(modifier = Modifier.weight(1f)) { LastXItemsList(Data.incomesList, 6, Color.Green)}
                Spacer(modifier = Modifier.width(UIVariables.Padding))
                BorderBox(modifier = Modifier.weight(1f)) { LastXItemsList(Data.expensesList, 6, Color.Red)}
                //BorderBox(modifier = Modifier.weight(1f)) {Finance_ui_kiadas(navHostController)}
            }

        }
        Button(onClick = {navHostController.navigate("money_income")},
            modifier = Modifier.align(Alignment.BottomStart)
        ) { Text("bevetel") }

        Button(onClick = {navHostController.navigate("money_expense")},
            modifier = Modifier.align(Alignment.BottomEnd)
        ) { Text("kiadas") }
    }

}

@Composable
fun Finance_ui_egyenleg(navHostController: NavHostController)
{
    val money = Data.osszpenz
    Column(
        modifier = Modifier
    )
    {
        Row ( modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
        )
        {
            HeaderText("Egyenleg: ")
            if(money < 0) Text("$money Ft", color = Color.Red)
            else if(money > 0) Text("$money Ft", color = Color.Green)
            else Text("$money Ft", color = Color.Black)

        }
    }
}

@Composable
fun Finance_ui_bevetel(navHostController: NavHostController)
{
    val bevetellist = Data.incomesList
    Column(modifier = Modifier.fillMaxWidth()) {
        HeaderText("Bevetelek")
        if(bevetellist.size >= 1)
        {
            for (i in bevetellist.size-1 downTo maxOf(0, bevetellist.size-6))
            {
                Text("+${bevetellist[i].amount} Ft", color = Color.Green)
            }
        }
    }
}

@Composable
fun Finance_ui_kiadas(navHostController: NavHostController)
{
    val kiadaslist = Data.expensesList
    Column(modifier = Modifier.fillMaxWidth()) {
        HeaderText("Kiadas")
        if(kiadaslist.size >= 1)
        {
            for (i in kiadaslist.size-1 downTo maxOf(0, kiadaslist.size-6))
            {
                Text("-${kiadaslist[i].amount} Ft", color = Color.Red)
            }
        }

    }
}

