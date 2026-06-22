package com.unram.asakv2.repository

import com.unram.asakv2.model.ChatMessage
import com.unram.asakv2.network.RetrofitClient

class AiTutorRepository {

    fun sendMessage(
        message: String,
        conversationHistory: List<ChatMessage>,
        callback: (Result<String>) -> Unit
    ) {
        
    }
}
