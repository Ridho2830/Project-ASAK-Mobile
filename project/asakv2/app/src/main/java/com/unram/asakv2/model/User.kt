package com.unram.asakv2.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val tagline: String = "",
    val xp: Int = 0,
    val level: Int = 1,
    val currentStage: Int = 1,
    val currentBagian: Int = 1,
    val streak: Int = 0,
    val maxStreak: Int = 0,
    val selectedAchievement: String = "",
    val selectedAchievements: List<String> = emptyList(),
    val completedStages: List<Int> = emptyList(),
    val unlockedLetters: List<String> = emptyList(),
    val unlockedWisata: List<String> = emptyList(),
    val unlockedBudaya: List<String> = emptyList(),
    val writingAccuracyHistory: List<Double> = emptyList(),
    val speakingAccuracyHistory: List<Double> = emptyList(),
    val earnedOneTimeXpEvents: List<String> = emptyList(),
    val unlockedAchievements: List<String> = emptyList(),
    val claimedAchievements: List<String> = emptyList(),
    val earnedMilestoneRewards: List<Int> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
