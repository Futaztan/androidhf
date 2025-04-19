package com.androidhf.ui.reuseable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Random UI változók hogy ne kelljen mindenhol mindent külön megadni

object UIVariables
{
    val Padding: Dp = 8.dp
    val Radius: Dp = 8.dp
    val BorderWidth: Dp = 4.dp
    val BoxBackgroundColor: Color = Color.DarkGray
    val HeaderText: TextUnit = 24.sp
    val AccentColor: Color = Color.Yellow
    val BackgroundColor: Color = Color(255f, 255f, 255f, 1f)

    // grafikon
    //Lenne ha lehetne módosítani a hátterét, de nyílván nem lehet mert egy UI elemnek minek is módosítani a színét, kinek kell az
    /*
    val GraphBackgroundColorLight: Color = Color(250f, 250f, 250f, 1f)
    val GraphBackgroundColorDark: Color = Color(20f, 20f, 20f, 1f)

    val GraphGridColorLight: Color = Color(200f, 200f, 200f, 1f)
    val GraphGridColorDark: Color = Color(60f, 60f, 60f, 1f)

    val GraphWaveColorLight: Color = Color(0f, 0f, 0f, 1f)
    val GraphWaveColorDark: Color = Color(200f, 200f, 200f, 1f)

    val GraphTextColorLight: Color = Color(0f, 0f, 0f, 0.9f)
    val GraphTextColorDark: Color = Color(245f, 245f, 245f, 0.9f)

    val GraphColorLight: Color = Color(255f, 0f, 0f, 0.4f)
    val GraphColorDark: Color = Color(255f, 0f, 0f, 0.4f)
    */
}