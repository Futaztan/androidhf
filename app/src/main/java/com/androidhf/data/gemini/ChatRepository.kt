package com.androidhf.data.gemini

import android.util.Log
import com.androidhf.data.gemini.models.Content
import com.androidhf.data.gemini.models.GeminiRequest
import com.androidhf.data.gemini.models.Part
import com.androidhf.ui.screens.ai.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiRepository {
    suspend fun sendMessageToGemini(messageHistory: List<ChatMessage>): String {
        return withContext(Dispatchers.IO) {
            try {
                val messages = messageHistory.map { chatMessage ->
                    if (chatMessage.sender == "user") {
                        Content(
                            role = "user",
                            parts = listOf(Part(chatMessage.content))
                        )
                    } else {
                        Content(
                            role = "model",
                            parts = listOf(Part(chatMessage.content))
                        )
                    }
                }

                val recentMessages = if (messages.size > 10) {
                    messages.takeLast(10)
                } else {
                    messages
                }

                val requestBody = GeminiRequest(
                    contents = recentMessages
                )

                val response = GeminiClient.service.generateContent(requestBody)

                if (response.isSuccessful) {
                    val candidates = response.body()?.candidates
                    if (candidates.isNullOrEmpty()) {
                        "Sajnos nem kaptam választ a modelltől."
                    } else {
                        val textContent = candidates[0].content.parts
                            .firstOrNull()?.text ?: "Üres válasz érkezett."
                        textContent
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Nincs részletes hibaüzenet"
                    Log.e("Gemini", "Hiba: ${response.code()} - ${response.message()} - $errorBody")
                    "Hiba történt: ${response.code()} ${response.message()}\nRészletek: $errorBody"
                }
            } catch (e: Exception) {
                Log.e("Gemini", "Exception: ${e.message}", e)
                "Hiba történt: ${e.message}"
            }
        }
    }
}