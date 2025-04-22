package com.androidhf.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.androidhf.ui.screens.ai.ChatMessage

object AiMessages {
    val messages = mutableStateListOf<ChatMessage>()

}