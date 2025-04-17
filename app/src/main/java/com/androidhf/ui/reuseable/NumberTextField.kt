package com.androidhf.ui.reuseable

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumberTextField(input: String, onInputChange: (String) -> Unit) {
    TextField(
        value = input,
        onValueChange = {
            if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                onInputChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}