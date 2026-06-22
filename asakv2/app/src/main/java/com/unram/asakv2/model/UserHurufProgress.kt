package com.unram.asakv2.model

/**
 * Data class progress per huruf per user — Melacak kemajuan belajar tiap huruf.
 */
data class UserHurufProgress(
    val userId: String = "",
    val hurufId: String = "",
    val totalAttempts: Int = 0,
    val correctAttempts: Int = 0,
    val accuracy: Float = 0f,
    val lastAttemptAt: Long = 0,
    val nextReviewAt: Long = 0,     // Untuk spaced repetition (SM-2)
    val easeFactor: Float = 2.5f,   // Faktor kemudahan SM-2
    val interval: Int = 1           // Interval review dalam hari
)
