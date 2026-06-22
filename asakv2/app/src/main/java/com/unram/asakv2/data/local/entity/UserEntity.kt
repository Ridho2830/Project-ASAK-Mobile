package com.unram.asakv2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val username: String,
    val totalXp: Long = 0,
    val currentStreak: Int = 0,
    val highestStreak: Int = 0,
    val diamondBalance: Int = 0,
    val isSynced: Boolean = true
)
