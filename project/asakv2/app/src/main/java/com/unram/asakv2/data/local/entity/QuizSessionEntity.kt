package com.unram.asakv2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "quiz_sessions")
data class QuizSessionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val startTime: Long,
    val endTime: Long,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val xpEarned: Int,
    val accuracy: Double,
    val isSynced: Boolean = false
)
