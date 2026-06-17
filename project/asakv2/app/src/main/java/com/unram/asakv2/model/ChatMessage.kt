package com.unram.asakv2.model

/**
 * Data class pesan AI Tutor — Representasi pesan dalam chat AI Tutor.
 */
data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val isFromUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
