package com.androidhf.ui.reuseable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Shadow


//Főcímeknek van kiemeléssel
@Composable
fun HeaderText(bemenet: String)
{
    Text(bemenet,
        fontSize = UIVar.HeaderText,
        fontWeight = FontWeight.Bold,
        color = UIVar.AccentColor,
        //ezt majd el kell dönteni hogy kell-e
        style = TextStyle(
            shadow = Shadow(color = UIVar.AccentColor.copy(0.35f, UIVar.AccentColor.red*0.7f,UIVar.AccentColor.green*0.7f,UIVar.AccentColor.blue*0.7f), offset = Offset(3f, 3f), blurRadius = 2f)
        )
    )
}