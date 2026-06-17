package com.unram.asakv2.repository

import com.unram.asakv2.model.ChatMessage
import com.unram.asakv2.network.RetrofitClient

/**
 * AiTutorRepository — Retrofit call ke Gemini API.
 * Mengelola komunikasi dengan Gemini API untuk fitur AI Tutor.
 * [RENDI]
 */
class AiTutorRepository {

    fun sendMessage(
        message: String,
        conversationHistory: List<ChatMessage>,
        callback: (Result<String>) -> Unit
    ) {
        // TODO: Kirim pesan ke Gemini API via Retrofit
        // TODO: Parse response dan return teks jawaban AI
    }
}
