package com.unram.asakv2.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GeminiApiService {

    @POST("v1beta/models/gemini-pro:generateContent")
    fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): Call<GeminiResponse>
}
