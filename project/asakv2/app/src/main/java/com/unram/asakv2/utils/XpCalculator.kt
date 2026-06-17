package com.unram.asakv2.utils

/**
 * XpCalculator — Hitung XP + multiplier streak.
 * Menghitung XP yang didapat berdasarkan skor quiz dan streak harian.
 * [RENDI]
 */
object XpCalculator {

    /**
     * Hitung XP berdasarkan jumlah jawaban benar dan streak.
     */
    fun calculateXp(correctAnswers: Int, streak: Int): Int {
        val baseXp = correctAnswers * Constants.XP_PER_CORRECT_ANSWER
        val multiplier = getStreakMultiplier(streak)
        return (baseXp * multiplier).toInt()
    }

    /**
     * Hitung multiplier berdasarkan streak harian.
     * Streak 0-1: 1.0x, Streak 2-4: 1.5x, Streak 5-9: 2.0x, Streak 10+: 2.5x
     */
    fun getStreakMultiplier(streak: Int): Float {
        return when {
            streak <= 1 -> 1.0f
            streak <= 4 -> 1.5f
            streak <= 9 -> 2.0f
            else -> 2.5f
        }
    }

    /**
     * Hitung total XP yang dibutuhkan untuk naik ke level tertentu.
     */
    fun xpRequiredForLevel(level: Int): Int {
        return when (level) {
            1 -> 0
            2 -> 100
            3 -> 300
            4 -> 600
            5 -> 1000
            6 -> 1500
            else -> 0
        }
    }

    /**
     * Hitung progress persentase menuju level berikutnya.
     */
    fun calculateLevelProgress(totalXp: Int, currentLevel: Int): Float {
        if (currentLevel >= Constants.MAX_LEVEL) return 1.0f
        val currentLevelXp = xpRequiredForLevel(currentLevel)
        val nextLevelXp = xpRequiredForLevel(currentLevel + 1)
        val xpInCurrentLevel = totalXp - currentLevelXp
        val xpNeeded = nextLevelXp - currentLevelXp
        return (xpInCurrentLevel.toFloat() / xpNeeded).coerceIn(0f, 1f)
    }
}
