package com.androidhf.ui.screens.finance.savingcards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.androidhf.R
import com.androidhf.data.Data.osszpenz
import com.androidhf.data.Savings
import com.androidhf.ui.reuseable.BorderBox
import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.UIVar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import androidx.compose.ui.graphics.ColorFilter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SavingCard_Income2(
    saving: Savings,
    onDismiss: () -> Unit
) {
    var showPopup by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val dismissState = rememberDismissState()

    if (showPopup) {
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
                    Column {
                        Text("Biztosan törölni szeretné?", color = Color.Black)
                        Row {
                            Button(onClick = {
                                showPopup=false
                                onDismiss()
                            }) {
                                Text("Igen")
                            }
                            Button(onClick = {
                                showPopup=false
                                coroutineScope.launch {
                                    dismissState.snapTo(DismissValue.Default)
                                }
                            }) {
                                Text("Nem")
                            }
                        }

                    }

                }
            }
        }
    }

    if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
        LaunchedEffect(saving.id) {
            showPopup=true
        }
    }

    SwipeToDismiss(
        state = dismissState,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red, shape = RoundedCornerShape(UIVar.Radius))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        },
        directions = setOf(DismissDirection.StartToEnd),
        dismissContent = {
            Content(saving)
        }
    )
}

@Composable
private fun Content(saving: Savings)
{
    if(saving.Amount > osszpenz)
    {

        BorderBox {
            if(saving.Title == "Easter" || saving.Title == "easter" || saving.Title == "Húsvét" || saving.Title == "húsvét") {
                Image(painter = painterResource(id = R.drawable.easter_background), contentDescription = "Easter", contentScale = Crop, colorFilter = ColorFilter.tint(color = Color.Black.copy(alpha = 0.4f), blendMode = BlendMode.Darken))
            }
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
                    Row {
                        Spacer(modifier = Modifier.weight(2f))
                        Box(modifier = Modifier.weight(8f)){
                            Text("${saving.StartDate}", modifier = Modifier.align(Alignment.CenterStart))
                            Text("${saving.EndDate}", modifier = Modifier.align(Alignment.CenterEnd))
                        }
                    }

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Date:", modifier = Modifier.weight(2f))
                    LinearProgressIndicator(
                        progress = DateProgressBar(saving).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(8.dp).weight(8f), color = Color.Green
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Balance:", modifier = Modifier.weight(2f))
                    LinearProgressIndicator(
                        progress = saving.Start.toFloat()/saving.Amount.toFloat(),
                        modifier = Modifier.fillMaxWidth().height(8.dp).weight(8f)
                    )
                }
            }
        }
    }
    else
    {
        BorderBox {
            Box()
            {
                Column {
                    Row {
                        Column(modifier = Modifier.weight(8f)) {
                            HeaderText(saving.Title)
                            Text("${saving.Amount} Ft")
                            Text("Successfully achieved!")
                        }
                        Box(modifier = Modifier.fillMaxHeight().weight(2f).align(Alignment.CenterVertically)) {
                            Icon(painter = painterResource(id = R.drawable.ic_check_48dp), contentDescription = "Home icon")
                        }
                    }
                }
            }
        }
    }
}

private fun DateProgressBar(saving: Savings): Float
{
    val startDate = saving.StartDate
    val endDate = saving.EndDate

    val currentDate = LocalDate.now()

    val totalDays = ChronoUnit.DAYS.between(startDate, endDate)

    val daysPassed = ChronoUnit.DAYS.between(startDate, currentDate)

    return if (totalDays > 0) daysPassed.toFloat() / totalDays.toFloat() else 0f
}