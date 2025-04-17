package com.androidhf.data.gemini

import com.androidhf.data.gemini.models.GeminiRequest
import com.androidhf.data.gemini.models.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GeminiService {
    @Headers(
        "Content-Type: application/json"
    )
        @POST("v1/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(@Body requestBody: GeminiRequest): Response<GeminiResponse>
}