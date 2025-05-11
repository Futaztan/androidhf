package com.androidhf.ui.screens.finance.savingcards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.androidhf.data.Savings
import com.androidhf.ui.reuseable.BorderBox
import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.UIVar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidhf.data.Transaction
import com.androidhf.ui.screens.finance.TransactionViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SavingCard_Income1(
    saving: Savings, //melyik savinget jelenítse meg
    onDismiss: () -> Unit, //visible = false -ot kell meghívni ha törölni akarjuk, enélkül nem tűnik el
    deleteAble: Boolean = true //Swipeal törölni szeretnénk akkor true, amúgy meg false
) {
    var showPopup by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val dismissState = rememberDismissState()

    if (showPopup) {
        Popup {
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 250.dp))
            {
                Box(
                    modifier = Modifier
                        .padding(40.dp)
                        .fillMaxWidth()
                        .border(4.dp, UIVar.boxBorderColor(), RoundedCornerShape(UIVar.Radius))
                        .background(MaterialTheme.colorScheme.onError, RoundedCornerShape(UIVar.Radius))
                        .padding(UIVar.Padding)
                        .align(Alignment.BottomCenter)
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Biztosan törölni szeretné?", modifier = Modifier.align(Alignment.CenterVertically), color = MaterialTheme.colorScheme.error)
                            Icon(painter = painterResource(id = R.drawable.ic_warning_48), contentDescription = "Warning", modifier = Modifier.align(Alignment.CenterVertically), tint = MaterialTheme.colorScheme.error)
                        }
                        Row {
                            Button(
                                onClick = {
                                    showPopup = false
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.weight(3f)
                            ) {
                                Text("Igen", color = MaterialTheme.colorScheme.onError) // szöveg szín
                            }
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                            Button(onClick = {
                                showPopup=false
                                coroutineScope.launch {
                                    dismissState.snapTo(DismissValue.Default)
                                }
                            }, modifier = Modifier.weight(7f)
                            ) {
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

    if (deleteAble)
    {
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
    else
    {
        Content(saving)
    }

}

@Composable
private fun Content(saving: Savings)
{
    val tViewModel: TransactionViewModel = hiltViewModel()
    val osszeg = tViewModel.balance.collectAsState().value
    if(!saving.Closed && LocalDate.now() < saving.EndDate)
    {

        BorderBox {
            if(saving.Title == "Easter" || saving.Title == "easter" || saving.Title == "Húsvét" || saving.Title == "húsvét") {
                Image(painter = painterResource(id = R.drawable.easter_background), contentDescription = "Easter", contentScale = Crop, colorFilter = ColorFilter.tint(color = Color.Black.copy(alpha = 0.4f), blendMode = BlendMode.Darken))
            }
            Column {
                Row {
                    Column(modifier = Modifier.weight(6.5f)) {
                        HeaderText(saving.Title)
                        Text(saving.Description, color = UIVar.onBoxColor())
                        if(ChronoUnit.DAYS.between(LocalDate.now(), saving.EndDate) in 0..7)
                        {
                            Row(modifier = Modifier.background(MaterialTheme.colorScheme.error, RoundedCornerShape(UIVar.Radius)), verticalAlignment = Alignment.CenterVertically){
                                Icon(painter = painterResource(id = R.drawable.ic_warning_16), contentDescription = "Warning", tint = MaterialTheme.colorScheme.onError)
                                Text("Deadline Inbound!", color = MaterialTheme.colorScheme.onError)
                                Icon(painter = painterResource(id = R.drawable.ic_warning_16), contentDescription = "Warning", tint = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                    Box(modifier = Modifier.fillMaxHeight().weight(3.5f)) {
                        Column( modifier = Modifier.align(Alignment.CenterEnd)) {
                            Text("${saving.Amount} Ft", fontWeight = FontWeight.ExtraBold, color = UIVar.onBoxColor(),modifier = Modifier.align(Alignment.End))
                            Box(modifier = Modifier.background(UIVar.onBoxColor(), RoundedCornerShape(UIVar.Radius)).padding(start = 3.dp, end = 3.dp).align(Alignment.End))
                            {
                                Text("Type: Hold", color = UIVar.boxColor())
                            }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth())
                {
                    Row {
                        Spacer(modifier = Modifier.weight(2f))
                        Box(modifier = Modifier.weight(8f)){
                            Text("${saving.StartDate}", modifier = Modifier.align(Alignment.CenterStart), color = UIVar.onBoxColor())
                            Text("${saving.EndDate}", modifier = Modifier.align(Alignment.CenterEnd), color = UIVar.onBoxColor())
                        }
                    }

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Date:", modifier = Modifier.weight(2f), color = UIVar.onBoxColor())
                    LinearProgressIndicator(
                        progress = DateProgressBar(saving).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(8.dp).weight(8f), color = Color.Green
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Balance:", modifier = Modifier.weight(2f), color = UIVar.onBoxColor())
                    LinearProgressIndicator(
                        progress = osszeg.toFloat()/saving.Amount.toFloat(),
                        modifier = Modifier.fillMaxWidth().height(8.dp).weight(8f)
                    )
                }
            }
        }
    }
    else if(!saving.Closed && (saving.Completed || (saving.Amount <= osszeg && LocalDate.now() >= saving.EndDate)))
    {
        saving.Completed = true
        saving.Closed = true
        BorderBox {
            Box()
            {
                Column {
                    Row {
                        Column(modifier = Modifier.weight(8f)) {
                            HeaderText(saving.Title)
                            Text("${saving.Amount} Ft", color = UIVar.onBoxColor())
                            Text("Successfully achieved!", color = UIVar.onBoxColor())
                            Box(modifier = Modifier.background(UIVar.onBoxColor(), RoundedCornerShape(UIVar.Radius)).padding(start = 4.dp, end = 4.dp))
                            {
                                Text("Type: Hold", color = UIVar.boxColor())
                            }
                        }
                        Box(modifier = Modifier.fillMaxHeight().weight(2f).align(Alignment.CenterVertically)) {
                            Icon(painter = painterResource(id = R.drawable.ic_check_48dp), contentDescription = "Tick Icon")
                        }
                    }
                }
            }
        }
    }
    else{
        saving.Failed = true
        saving.Closed = true
        BorderBox {
            Box()
            {
                Column {
                    Row {
                        Column(modifier = Modifier.weight(8f)) {
                            HeaderText(saving.Title)
                            Text("${saving.Amount} Ft", color = UIVar.onBoxColor())
                            Text("Failed to achieve!", color = UIVar.onBoxColor())
                            Box(modifier = Modifier.background(UIVar.onBoxColor(), RoundedCornerShape(UIVar.Radius)).padding(start = 4.dp, end = 4.dp))
                            {
                                Text("Type: Hold", color = UIVar.boxColor())
                            }
                        }
                        Box(modifier = Modifier.fillMaxHeight().weight(2f).align(Alignment.CenterVertically)) {
                            Icon(painter = painterResource(id = R.drawable.ic_cross_48dp), contentDescription = "Cross Icon")
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