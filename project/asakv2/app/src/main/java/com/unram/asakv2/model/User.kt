package com.unram.asakv2.model

/**
 * Data class user — Representasi data pengguna di Firestore.
 */
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val selectedAchievement: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
