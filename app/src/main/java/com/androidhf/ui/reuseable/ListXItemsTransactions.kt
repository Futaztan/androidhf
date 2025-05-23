package com.androidhf.ui.reuseable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
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
fun ListXItemsTransactions(
    //LEGALÁBB AZ EGYIKET MEG KELL ADNI
    items: State<List<Transaction>>? = null, //melyik adatokat listázza
    listItems: List<Transaction>? = null, //ha null akkor ez fog listázni

    count: Int, //Mennyit adjon vissza, ha -1, akkor mindent visszaad
    _color : Color, //listázás színe
    _modifier : Modifier = Modifier, //modifierek
    _fitMaxWidth: Boolean = false, //kitöltse a rendelkezésre álló helyet, ha két panel kell egymás mellé akkor false és kívül kell megadni weightet
    reversed: Boolean = false //milyen sorrendben írja ki
)
{
    if(items != null)
    {
        val firstItems = if(count == -1)
        {
            if(reversed) items.value.asReversed()
            else items.value
        }
        else
        {
            if(reversed) items.value.takeLast(count).asReversed()
            else items.value.takeLast(count)

        }
        var color: Color = _color
        if(color == Color.Green)
        {
            color = UIVar.colorGreen()
        }
        else if(color == Color.Red)
        {
            if(!isSystemInDarkTheme())
            {
                color = UIVar.colorRed()
            }
        }
        Panel(fitMaxWidth = _fitMaxWidth, modifier = _modifier, centerItems = false) {
            Column(modifier = _modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                firstItems.forEach { item ->
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                            {
                                append("${item.amount} Ft ")
                            }
                            append("${item.category.getDisplayName(LocalContext.current)} ${item.frequency} ${item.description} ${item.date} ${if(item.time.hour > 10) item.time.hour else "0"+item.time.hour}:${if(item.time.minute > 10) item.time.minute else "0"+item.time.minute}:${if(item.time.second > 10) item.time.second else "0"+item.time.second}")
                        },
                        modifier = Modifier.padding(8.dp),
                        color = color
                    )
                }
            }
        }
    }
    else if (listItems != null)
    {
        val firstItems = if(count == -1)
        {
            if(reversed) listItems.asReversed()
            else listItems
        }
        else
        {
            if(reversed) listItems.takeLast(count).asReversed()
            else listItems.takeLast(count)

        }
        var color: Color = _color
        if(color == Color.Green)
        {
            color = UIVar.colorGreen()
        }
        else if(color == Color.Red)
        {
            if(!isSystemInDarkTheme())
            {
                color = UIVar.colorRed()
            }
        }
        Panel(fitMaxWidth = _fitMaxWidth, modifier = _modifier, centerItems = false) {
            Column(modifier = _modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                firstItems.forEach { item ->
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                            {
                                append("${item.amount} Ft ")
                            }
                            append("${item.category.getDisplayName(LocalContext.current)} ${item.frequency} ${item.description} ${item.date} ${if(item.time.hour > 10) item.time.hour else "0"+item.time.hour}:${if(item.time.minute > 10) item.time.minute else "0"+item.time.minute}:${if(item.time.second > 10) item.time.second else "0"+item.time.second}")
                        },
                        modifier = Modifier.padding(8.dp),
                        color = color
                    )
                }
            }
        }
    }
}