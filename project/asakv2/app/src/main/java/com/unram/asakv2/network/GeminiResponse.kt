package com.unram.asakv2.network

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiResponseContent?
)

data class GeminiResponseContent(
    val parts: List<GeminiResponsePart>?,
    val role: String?
)

data class GeminiResponsePart(
    val text: String?
)
