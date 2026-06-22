package com.unram.asakv2.model

data class CnnRequest(
    val image: String,
    val huruf: String,
    val sizePrefix: Int = 2
)

data class CnnResponse(
    val score: Float,
    val label: String? = null
)