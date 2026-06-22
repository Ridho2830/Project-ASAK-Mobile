package com.unram.asakv2.ai

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.unram.asakv2.network.GradioQueueRequest
import com.unram.asakv2.network.RetrofitClient
import com.unram.asakv2.utils.Constants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID

class VoiceRecognizer(private val context: Context) {

    companion object {
        private const val TAG = "VoiceRecognizer"
    }

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): File {
        val file = File(context.cacheDir, "voice_quiz_${System.currentTimeMillis()}.m4a")
        outputFile = file

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }

        return file
    }

    fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: Exception) {
            Log.e(TAG, "Gagal stop recording", e)
        }
    }

    suspend fun recognizeTarget(audioFile: File, targetLabel: String): Float {
        return try {
            if (!audioFile.exists() || audioFile.length() == 0L) {
                Log.e(TAG, "File audio tidak ada atau kosong")
                return 0f
            }

            // ── Step 1: Upload file ─────────────────────────────
            val filePart = MultipartBody.Part.createFormData(
                "files", audioFile.name,
                audioFile.asRequestBody("audio/mp4".toMediaType())
            )
            val uploadResp = RetrofitClient.voiceApi.uploadAudio(filePart)
            if (!uploadResp.isSuccessful) {
                Log.e(TAG, "Upload gagal: ${uploadResp.code()} - ${uploadResp.errorBody()?.string()}")
                return 0f
            }
            val tmpPath = uploadResp.body()?.firstOrNull() ?: return 0f
            Log.d(TAG, "Upload sukses: $tmpPath")

            // ── Step 2: Join queue ──────────────────────────────
            val sessionHash = UUID.randomUUID().toString().replace("-", "").take(12)
            val target = targetLabel.lowercase()

            // Input order dari config: inputs=[15, 14] = [audio, dropdown]
            val audioPayload = mapOf(
                "path" to tmpPath,
                "meta" to mapOf("_type" to "gradio.FileData")
            )

            val joinResp = RetrofitClient.voiceApi.joinQueue(
                GradioQueueRequest(
                    data = listOf(audioPayload, target),
                    fn_index = 1,  // predict_target = dependency id 1
                    session_hash = sessionHash
                )
            )
            if (!joinResp.isSuccessful) {
                Log.e(TAG, "Join queue gagal: ${joinResp.code()} - ${joinResp.errorBody()?.string()}")
                return 0f
            }
            Log.d(TAG, "Join queue sukses, session: $sessionHash")

            // ── Step 3: Poll hasil SSE ──────────────────────────
            // Retry beberapa kali sampai dapat hasil
            repeat(30) { attempt ->
                kotlinx.coroutines.delay(1000L)

                val pollResp = RetrofitClient.voiceApi.pollData(sessionHash)
                if (!pollResp.isSuccessful) return@repeat

                val raw = pollResp.body()?.string() ?: return@repeat
                Log.d(TAG, "Poll attempt $attempt: $raw")

                // Cari baris "data:" yang mengandung output
                // SSE format: "data: {...}"
                val lines = raw.lines()
                for (line in lines) {
                    if (!line.startsWith("data:")) continue
                    val json = line.removePrefix("data:").trim()
                    if (json.contains("\"msg\":\"process_completed\"")) {
                        // Parse skor dari output markdown
                        // output[0] = teks markdown hasil predict_target
                        val regex = Regex("""(\d+(?:\.\d+)?)\s*%""")
                        val score = regex.find(json)?.groupValues?.get(1)?.toFloatOrNull()
                        if (score != null) {
                            Log.d(TAG, "Score: $score")
                            return score
                        }
                    }
                }
            }

            Log.e(TAG, "Timeout polling hasil")
            0f

        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            0f
        }
    }

    fun isAccurate(score: Float): Boolean {
        return score >= Constants.VOICE_ACCURACY_THRESHOLD
    }

    fun cleanup() {
        outputFile?.delete()
        outputFile = null
    }
}



