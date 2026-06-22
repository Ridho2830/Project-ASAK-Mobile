package com.unram.asakv2.model

data class QuizResult(
    val expEarned: Int,
    val isCorrect: Boolean,
    val hurufId: String = ""
)