package com.androidhf.ui.reuseable

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Shadow


//Főcímeknek van kiemeléssel
@Composable
fun HeaderText(bemenet: String, modifier: Modifier = Modifier)
{
    Text(bemenet,
        fontSize = UIVar.HeaderText,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.tertiary,
        //ezt majd el kell dönteni hogy kell-e
        modifier = modifier
    )
}