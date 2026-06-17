package com.unram.asakv2.ai

import com.unram.asakv2.network.HuggingFacePrediction
import java.io.File

/**
 * VoiceRecognizer — Kirim audio ke Hugging Face (Voice).
 * Mengirim file audio dari perekaman suara ke model Voice di Hugging Face.
 * [RENDI]
 */
class VoiceRecognizer {

    fun recognize(audioFile: File, callback: (Result<List<HuggingFacePrediction>>) -> Unit) {
        // TODO: Kirim file audio ke Hugging Face API via RetrofitClient
        // TODO: Parse dan return hasil prediksi
    }

    fun startRecording(outputFile: File) {
        // TODO: Mulai merekam audio menggunakan MediaRecorder
    }

    fun stopRecording(): File? {
        // TODO: Hentikan perekaman dan return file audio
        return null
    }
}
