package com.androidhf.ui.reuseable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.androidhf.data.datatypes.Transaction

//Dávid csinálta, de kiszedtem reuseablebe és kiegészítettem, egy panellel
//Az összeget bold-al írja a többi adatot pedig simán
//Jelenleg kiírja a megadott tranzakció típusok között a countban megadott legújabbat egy panelen belül
@Composable
fun ListXItemsTransactionsMonthly(
    items: State<List<Transaction>>, //melyik adatokat listázza
    count: Int, //Mennyit adjon vissza, ha -1 akkor mindet
    _color : Color, //listázás színe
    _modifier : Modifier = Modifier, //modifierek
    _fitMaxWidth: Boolean = false, //kitöltse a rendelkezésre álló helyet, ha két panel kell egymás mellé akkor false és kívül kell megadni weightet
    reversed: Boolean = false //milyen sorrendben írja ki
)
{
    var color: Color = _color
    if(color == Color.Green)
    {
        color = UIVar.colorGreen()
    }
    else if(color == Color.Red)
    {
        color = UIVar.colorRed()
    }

    val firstItems = if(count == -1)
    {
        //ha -1 akkor mindent visszaad
        if (reversed) {
            items.value.asReversed()
        } else items.value
    }
    else
    {
        if(reversed){
            items.value.takeLast(count).asReversed()
        } else items.value.takeLast(count)
    }

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
                                append("${item.category.displayName} ${item.description} ${if(item.time.hour > 10) item.time.hour else "0"+item.time.hour}:${if(item.time.minute > 10) item.time.minute else "0"+item.time.minute}:${if(item.time.second > 10) item.time.second else "0"+item.time.second}")
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
                                append("${item.category.displayName} ${item.description} ${if(item.time.hour > 10) item.time.hour else "0"+item.time.hour}:${if(item.time.minute > 10) item.time.minute else "0"+item.time.minute}:${if(item.time.second > 10) item.time.second else "0"+item.time.second}")
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