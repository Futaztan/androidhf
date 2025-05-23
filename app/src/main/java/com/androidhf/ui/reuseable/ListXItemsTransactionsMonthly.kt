package com.androidhf.ui.reuseable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
        Panel(fitMaxWidth = false, modifier = _modifier, shadow = 0.dp) {
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
                        Text(item.amount.toString() + "Ft", modifier = Modifier.padding(start = UIVar.Padding), color = color, fontWeight = FontWeight.ExtraBold)
                        Text("${item.category.getDisplayName(LocalContext.current)} - ${item.description}", modifier = Modifier.padding(start = UIVar.Padding), color = color)
                        Spacer(modifier = Modifier.height(UIVar.Padding))
                    }
                    else
                    {
                        Text(item.amount.toString() + "Ft", modifier = Modifier.padding(start = UIVar.Padding), color = color, fontWeight = FontWeight.ExtraBold)
                        Text("${item.category.getDisplayName(LocalContext.current)} - ${item.description}", modifier = Modifier.padding(start = UIVar.Padding), color = color)
                        Spacer(modifier = Modifier.height(UIVar.Padding))
                    }
                }
            }
        }
    }
}