package com.unram.asakv2.model

data class UserHurufProgress(
    val userId: String = "",
    val hurufId: String = "",
    val totalAttempts: Int = 0,
    val correctAttempts: Int = 0,
    val accuracy: Float = 0f,
    val lastAttemptAt: Long = 0,
    val nextReviewAt: Long = 0,     
    val easeFactor: Float = 2.5f,   
    val interval: Int = 1           
)
