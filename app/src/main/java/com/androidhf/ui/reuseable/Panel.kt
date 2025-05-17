package com.androidhf.ui.reuseable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

//gyakorlatileg egy doboz keret nélkül
//Ez módosítható modifierrel
@Composable
fun Panel(
    modifier: Modifier = Modifier, //modifierek
    backgroundColor: Color = UIVar.panelColor(), //panel háttere
    cornerRadius: Dp = UIVar.Radius, //mennyire legyen lekerekítve
    padding: Dp = UIVar.Padding, //mekkora legyen a belső padding
    centerItems: Boolean = true, //legyenek-e középre igazítve a belső elemek
    fitMaxWidth: Boolean = true, //kitöltse a maximális helyet ami rendelkezésre áll, ha kettő oszlopot kell egymás mellé tenni akkor ne
    shadow: Dp = 3.dp, //0 hogy ne legyen
    uibemenet: @Composable () -> Unit //ide a jön a ui amit bele kell tenni
)
{
    val boxModifier = modifier
        .shadow(
            elevation = shadow,
            shape = RoundedCornerShape(cornerRadius)
        )
        .background(backgroundColor, RoundedCornerShape(cornerRadius))
        .padding(padding)
        .then(if (fitMaxWidth) Modifier.fillMaxWidth() else Modifier)

    val alignment = if (centerItems) Alignment.Center else Alignment.TopStart
    Box(modifier = boxModifier,
        contentAlignment = alignment
    )
    {
        uibemenet()
    }
}