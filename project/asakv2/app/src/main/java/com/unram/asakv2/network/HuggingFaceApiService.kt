package com.unram.asakv2.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * HuggingFaceApiService — Retrofit interface untuk Hugging Face API.
 * Mendefinisikan endpoint untuk inference model CNN (handwriting) dan Voice recognition.
 * [RENDI]
 */
interface HuggingFaceApiService {

    @Multipart
    @POST("{modelId}")
    fun predictHandwriting(
        @Path("modelId", encoded = true) modelId: String,
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<List<HuggingFacePrediction>>

    @Multipart
    @POST("{modelId}")
    fun predictVoice(
        @Path("modelId", encoded = true) modelId: String,
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<List<HuggingFacePrediction>>
}

/**
 * Data class hasil prediksi dari Hugging Face.
 */
data class HuggingFacePrediction(
    val label: String = "",
    val score: Float = 0f
)
