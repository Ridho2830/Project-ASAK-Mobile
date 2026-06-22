package com.unram.asakv2.model

data class QuizPlan(
    val tipe: Int,
    val huruf: String = "",
    val pasangan: Pair<String, String>? = null,
    val expFull: Int,
    val isStreakMode: Boolean = true
)