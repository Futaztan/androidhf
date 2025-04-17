package com.androidhf.ui.reuseable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
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
fun LastXItemsList(
    items: SnapshotStateList<Transaction>, //melyik adatokat listázza
    count: Int, //Mennyit adjon vissza
    _color : Color, //listázás színe
    _modifier : Modifier = Modifier, //modifierek
    _fitMaxWidth: Boolean = false //kitöltse a rendelkezésre álló helyet, ha két panel kell egymás mellé akkor false és kívül kell megadni weightet
)
{
    val firstItems = items.takeLast(count).asReversed()

    Panel(fitMaxWidth = false, modifier = _modifier) {
        Column(modifier = _modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            firstItems.forEach { item ->
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                        {
                            append("${item.amount} Ft ")
                        }
                        append("${item.category} ${item.reason} ${item.date} ${item.time}")
                    },
                    modifier = Modifier.padding(8.dp),
                    color = _color
                )
            }
        }
    }
}