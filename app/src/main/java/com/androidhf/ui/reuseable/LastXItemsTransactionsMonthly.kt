package com.androidhf.ui.reuseable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.androidhf.data.Transaction

//Dávid csinálta, de kiszedtem reuseablebe és kiegészítettem, egy panellel
//Az összeget bold-al írja a többi adatot pedig simán
//Jelenleg kiírja a megadott tranzakció típusok között a countban megadott legújabbat egy panelen belül
@Composable
fun LastXItemsTransactionsMonthly(
    items: State<List<Transaction>>, //melyik adatokat listázza
    count: Int, //Mennyit adjon vissza
    _color : Color, //listázás színe
    _modifier : Modifier = Modifier, //modifierek
    _fitMaxWidth: Boolean = false //kitöltse a rendelkezésre álló helyet, ha két panel kell egymás mellé akkor false és kívül kell megadni weightet
)
{
    var color: Color = _color
    if(color == Color.Green)
    {
        if(!isSystemInDarkTheme())
        {
            color = Color(0, 165, 0)
        }
    }
    else if(color == Color.Red)
    {
        if(!isSystemInDarkTheme())
        {
            color = Color(185, 0, 0)
        }
    }
    val firstItems = items.value.takeLast(count).asReversed()

    if(firstItems.isNotEmpty())
    {
        Panel(fitMaxWidth = false, modifier = _modifier) {
            Column(modifier = _modifier) {
                var prevYear = firstItems[0].date.year
                var prevMonth = firstItems[0].date.month
                Text("${prevYear}-${prevMonth}")
                firstItems.forEach { item ->
                    if (prevYear != item.date.year || prevMonth != item.date.month)
                    {
                        prevMonth = item.date.month
                        prevYear = item.date.year
                        Text("${prevYear}-${prevMonth}")
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold))
                                {
                                    append("${item.amount} Ft ")
                                }
                                append("${item.category} ${item.description} ${item.time.hour}:${item.time.minute}:${item.time.second}")
                            },
                            modifier = Modifier.padding(8.dp),
                            color = color
                        )
                    }
                    else
                    {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold))
                                {
                                    append("${item.amount} Ft ")
                                }
                                append("${item.category} ${item.description} ${item.time.hour}:${item.time.minute}:${item.time.second}")
                            },
                            modifier = Modifier.padding(8.dp),
                            color = color
                        )
                    }
                }
            }
        }
    }
}