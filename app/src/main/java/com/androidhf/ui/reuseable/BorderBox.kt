package com.androidhf.ui.reuseable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


//egy doboz kerettel
@Composable
fun BorderBox(modifier: Modifier = Modifier, backgroundColor: Color = UIVar.boxColor(), borderSize: Dp = UIVar.BorderWidth, uibemenet: @Composable () -> Unit)
{
    Box(modifier = modifier
        .border(borderSize, UIVar.boxBorderColor(), RoundedCornerShape(UIVar.Radius))
        .shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(UIVar.Padding),
            clip = false
        )
        .background(backgroundColor, RoundedCornerShape(UIVar.Radius))
        .padding(UIVar.Padding)
    )
    {
        uibemenet()
    }
}