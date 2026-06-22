package com.unram.asakv2.model

data class QuizSession(
    val sessionId: String = "",
    val userId: String = "",
    val startedAt: Long = 0,
    val finishedAt: Long = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val score: Int = 0,
    val xpEarned: Int = 0,
    val answers: List<QuizAnswer> = emptyList()
)

data class QuizAnswer(
    val hurufId: String = "",
    val quizType: Int = 0,          
    val userAnswer: String = "",
    val correctAnswer: String = "",
    val isCorrect: Boolean = false,
    val responseTimeMs: Long = 0
)
