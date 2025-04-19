package com.androidhf.ui.screens.finance.savingcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androidhf.data.Data
import com.androidhf.data.Data.Osszpenz
import com.androidhf.data.Savings
import com.androidhf.ui.reuseable.BorderBox
import com.androidhf.ui.reuseable.HeaderText
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun SavingCard_Income1(saving: Savings)
{
    BorderBox {
        Column {
            Row {
                Column(modifier = Modifier.weight(8f)) {
                    HeaderText(saving.Title)
                    Text(saving.Description)
                }
                Box(modifier = Modifier.fillMaxHeight().weight(2f)) {
                    Text("${saving.Amount} Ft", modifier = Modifier.align(Alignment.Center))
                }
            }
            Box(modifier = Modifier.fillMaxWidth())
            {
                Text("${saving.EndDate}", modifier = Modifier.align(Alignment.CenterEnd))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Date:", modifier = Modifier.weight(2f))
                LinearProgressIndicator(
                    progress = DateProgressBar(saving).coerceIn(0f, 1f), // biztosítsd, hogy 0 és 1 között maradjon
                    modifier = Modifier.fillMaxWidth().height(8.dp).weight(8f), color = Color.Green
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Balance:", modifier = Modifier.weight(2f))
                LinearProgressIndicator(
                    progress = Data.osszpenz.toFloat()/saving.Amount.toFloat(),
                    modifier = Modifier.fillMaxWidth().height(8.dp).weight(8f)
                )
            }
        }
    }
}

fun DateProgressBar(saving: Savings): Float
{
    // Kezdő és befejező dátum
    val startDate = saving.StartDate
    val endDate = saving.EndDate

    // Aktuális dátum
    val currentDate = LocalDate.now()

    // Számold ki a teljes időtartamot (napokban)
    val totalDays = ChronoUnit.DAYS.between(startDate, endDate)

    // Számold ki, hogy az aktuális dátumhoz képest hány nap telt el
    val daysPassed = ChronoUnit.DAYS.between(startDate, currentDate)

    // Számítsd ki a progressz bar értékét
    return if (totalDays > 0) daysPassed.toFloat() / totalDays.toFloat() else 0f
}