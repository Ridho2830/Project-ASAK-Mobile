package com.unram.asakv2.network

/**
 * GeminiRequest — Data class request body untuk Gemini API.
 * [RENDI]
 */
data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

data class GeminiPart(
    val text: String
)
