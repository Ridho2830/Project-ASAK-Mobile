package com.unram.asakv2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_huruf_progress")
data class UserHurufProgressEntity(
    @PrimaryKey
    val id: String, 
    val userId: String,
    val hurufId: String,
    val srsLevel: Int = 0,
    val nextReviewDate: Long = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val isSynced: Boolean = false
)
