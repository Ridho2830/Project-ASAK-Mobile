package com.unram.asakv2.network

import com.unram.asakv2.model.CnnRequest
import com.unram.asakv2.model.CnnResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

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

    @POST("cek-akurasi-cnn")
    suspend fun checkHandwriting(
        @Body request: CnnRequest
    ): Response<CnnResponse>

    @Multipart
    @POST("gradio_api/upload")
    suspend fun uploadAudio(
        @Part file: MultipartBody.Part
    ): Response<List<String>>

    @POST("gradio_api/queue/join")
    suspend fun joinQueue(
        @Body body: GradioQueueRequest
    ): Response<GradioJoinResponse>

    @GET("gradio_api/queue/data")
    suspend fun pollData(
        @Query("session_hash") sessionHash: String
    ): Response<ResponseBody>
}

data class HuggingFacePrediction(
    val label: String = "",
    val score: Float = 0f
)

data class GradioQueueRequest(
    val data: List<Any?>,
    val fn_index: Int,
    val session_hash: String,
    val event_data: Any? = null
)

data class GradioJoinResponse(
    val event_id: String? = null
)
