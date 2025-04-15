package com.androidhf.ui.reuseable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

//gyakorlatileg egy doboz keret nélkül
//Ez módosítható modifierrel
@Composable
fun Panel(
    modifier: Modifier = Modifier,
    ui_bemenet: @Composable () -> Unit,
    backgroundColor: Color = UIVariables.BoxBackgroundColor,
    cornerRadius: Dp = UIVariables.Radius,
    padding: Dp = UIVariables.Padding
)
{
    Box(modifier = modifier
        .background(backgroundColor, RoundedCornerShape(cornerRadius))
        .padding(padding)
    )
    {
        ui_bemenet()
    }
}