package com.unram.asakv2.network

import com.unram.asakv2.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient — Singleton Retrofit + OkHttp builder.
 * Menyediakan instance Retrofit untuk Gemini API, Hugging Face API, dan backend local Ktor.
 */
object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val hfOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val geminiApiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.GEMINI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    val huggingFaceApiService: HuggingFaceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.HUGGING_FACE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceApiService::class.java)
    }

    val apiService: com.unram.asakv2.data.remote.ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.13:8080/") // Backend Ktor
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(com.unram.asakv2.data.remote.ApiService::class.java)
    }

    val cnnApi: HuggingFaceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.CNN_API_URL)
            .client(hfOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceApiService::class.java)
    }

    val voiceApi: HuggingFaceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.HF_SPACE_URL)
            .client(hfOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceApiService::class.java)
    }
}
