package com.androidhf.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidhf.data.AiMessages
import com.androidhf.data.gemini.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AIViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(userMessage: String, messages: List<ChatMessage>) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val aiResponse = GeminiRepository.sendMessageToGemini(messages)
                AiMessages.messages.add(
                    ChatMessage("AI", aiResponse, System.currentTimeMillis())
                )
            } catch (e: Exception) {
                AiMessages.messages.add(
                    ChatMessage("AI", "Sajnos hiba történt: ${e.message}", System.currentTimeMillis())
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}