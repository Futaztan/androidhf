package com.androidhf.ui.reuseable

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


//Főcímeknek van kiemeléssel
@Composable
fun HeaderText(bemenet: String)
{

    Text(bemenet,
        fontSize = UIVariables.HeaderText,
        color = UIVariables.AccentColor
    )
}