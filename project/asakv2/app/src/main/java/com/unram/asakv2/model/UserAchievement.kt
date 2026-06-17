package com.unram.asakv2.model

/**
 * Data class achievement milik user — Melacak achievement yang dimiliki user.
 */
data class UserAchievement(
    val userId: String = "",
    val achievementId: String = "",
    val achievement: Achievement? = null,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0,
    val isDisplayed: Boolean = false // Apakah ditampilkan di profil
)
