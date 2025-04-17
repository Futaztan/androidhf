package com.androidhf.data.gemini

import com.androidhf.data.gemini.models.GeminiRequest
import com.androidhf.data.gemini.models.GeminiResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    private const val API_KEY = "AIzaSyCzz0EysqNtqZT-PdQ9gcOvGWH7BE_3Kkw"

    private val queryParamInterceptor = Interceptor { chain ->
        val original = chain.request()
        val originalHttpUrl = original.url()

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("key", API_KEY)
            .build()

        val requestBuilder = original.newBuilder()
            .url(url)

        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(queryParamInterceptor)
        .build()

    val service: GeminiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiService::class.java)
    }
}