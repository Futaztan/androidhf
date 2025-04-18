package com.androidhf.ui.reuseable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


//egy doboz kerettel
@Composable
fun BorderBox(modifier: Modifier = Modifier, uibemenet: @Composable () -> Unit)
{
    Box(modifier = modifier
        .border(UIVariables.BorderWidth, Color.Red, RoundedCornerShape(UIVariables.Radius))
        .background(UIVariables.BoxBackgroundColor, RoundedCornerShape(UIVariables.Radius))
        .padding(UIVariables.Padding)
    )
    {
        uibemenet()
    }
}