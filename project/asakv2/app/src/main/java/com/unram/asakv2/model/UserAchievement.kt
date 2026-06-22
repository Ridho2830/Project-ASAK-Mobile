package com.unram.asakv2.model

data class UserAchievement(
    val userId: String = "",
    val achievementId: String = "",
    val achievement: Achievement? = null,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0,
    val isDisplayed: Boolean = false, 
    val progress: Int = 0
)
