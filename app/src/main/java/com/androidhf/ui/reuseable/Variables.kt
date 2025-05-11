package com.androidhf.ui.reuseable

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Random UI változók hogy ne kelljen mindenhol mindent külön megadni

object UIVar
{
    var topBarTitle by mutableStateOf("Home")

    val Padding: Dp = 8.dp
    val Radius: Dp = 8.dp
    val BorderWidth: Dp = 4.dp
    val BoxBackgroundColor: Color = Color.DarkGray
    val HeaderText: TextUnit = 24.sp
    val AccentColor: Color = Color.Yellow
    val BackgroundColor: Color = Color(255f, 255f, 255f, 1f)

    //Ezeket használjátok majd színek beállítására ha lehet ne a fenti cuccokat
    //Használat pl ... Modifier.background(UIVar.boxColor())
    //Minden elemhez két szín és két függvény tartozik az első az maga a háttér vagyis a fő szín pl: boxColor() és ehhez tartozik egy másik szín
    //ami meg a rajta lévő elemeket színezi, többnyire a szöveget ez meg egy "on"-al kezdődik pl onBoxColor()
    @Composable
    fun boxColor(): Color = MaterialTheme.colorScheme.primaryContainer
    @Composable
    fun onBoxColor(): Color = MaterialTheme.colorScheme.onPrimaryContainer
    @Composable
    fun boxBorderColor(): Color = MaterialTheme.colorScheme.onPrimaryContainer


    @Composable
    fun panelColor(): Color = MaterialTheme.colorScheme.primaryContainer
    @Composable
    fun onPanelColor(): Color = MaterialTheme.colorScheme.onPrimaryContainer



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