package com.unram.asakv2.repository

import com.unram.asakv2.model.Achievement
import com.unram.asakv2.model.UserAchievement

/**
 * AchievementRepository — Load & update achievement user.
 * Mengelola operasi baca/tulis data achievement pengguna.
 * [DESTI]
 */
class AchievementRepository {

    fun getAllAchievements(callback: (Result<List<Achievement>>) -> Unit) {
        // TODO: Load semua achievement dari Firestore
    }

    fun getUserAchievements(userId: String, callback: (Result<List<UserAchievement>>) -> Unit) {
        // TODO: Load achievement milik user dari Firestore
    }

    fun unlockAchievement(userId: String, achievementId: String, callback: (Result<Boolean>) -> Unit) {
        // TODO: Unlock achievement untuk user
    }

    fun setDisplayAchievement(userId: String, achievementId: String, callback: (Result<Boolean>) -> Unit) {
        // TODO: Set achievement yang ditampilkan di profil
    }
}
