package com.androidhf.data.gemini.models

data class GeminiResponse(
    val candidates: List<Candidate>,
    val promptFeedback: PromptFeedback?
)

data class Candidate(
    val content: Content,
    val finishReason: String,
    val index: Int,
    val safetyRatings: List<SafetyRating>?
)

data class PromptFeedback(
    val safetyRatings: List<SafetyRating>?
)

data class SafetyRating(
    val category: String,
    val probability: String
)