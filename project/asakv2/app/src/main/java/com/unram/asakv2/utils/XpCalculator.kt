package com.unram.asakv2.utils

enum class XpEvent {
    DAFTAR,             
    UBAH_NAMA,          
    UBAH_TAGLINE,       
    UBAH_FOTO,
    PENGGUNAAN_AR,      
    UBAH_ACHIEVEMENT,   
    BELAJAR             
}

object XpCalculator {

    val ONE_TIME_XP = mapOf(
        "daftar" to 20,
        "ubah_nama" to 50,
        "ubah_tagline" to 50,
        "ubah_foto" to 50,
        "penggunaan_ar" to 100,
        "ubah_achievement" to 50,
        "belajar" to 100
    )

    val MILESTONE_XP = mapOf(
        3 to 50,
        6 to 100,
        9 to 200,
        12 to 350,
        15 to 550,
        18 to 800,
        21 to 1100,
        25 to 1500
    )

    private val oneTimeExp: Map<XpEvent, Int> = mapOf(
        XpEvent.DAFTAR to 20,
        XpEvent.UBAH_NAMA to 50,
        XpEvent.UBAH_TAGLINE to 50,
        XpEvent.UBAH_FOTO to 50,
        XpEvent.PENGGUNAAN_AR to 100,
        XpEvent.UBAH_ACHIEVEMENT to 50,
        XpEvent.BELAJAR to 100
    )

    private val achievementMilestoneExp: Map<Int, Int> = mapOf(
        3 to 50,
        6 to 100,
        9 to 200,
        12 to 350,
        15 to 550,
        18 to 800,
        20 to 1000
    )

    private val stageCompletionExp: Map<Int, Int> = mapOf(
        1 to 100,
        2 to 150,
        3 to 200,
        4 to 250,
        5 to 300,
        6 to 350
    )

    fun calculateXp(correctAnswers: Int, streak: Int): Int {
        val baseXp = correctAnswers * Constants.XP_PER_CORRECT_ANSWER
        val multiplier = getStreakMultiplier(streak)
        return (baseXp * multiplier).toInt()
    }

    fun getStreakMultiplier(streak: Int): Float {
        return when {
            streak <= 1 -> 1.0f
            streak <= 4 -> 1.5f
            streak <= 9 -> 2.0f
            else -> 2.5f
        }
    }

    fun xpRequiredForLevel(level: Int): Int {
        return when (level) {
            1 -> 0
            2 -> 50
            3 -> 150
            4 -> 300
            5 -> 500
            6 -> 750
            7 -> 950
            8 -> 1300
            9 -> 1700
            10 -> 2150
            11 -> 2650
            12 -> 3200
            13 -> 3800
            14 -> 4450
            15 -> 5150
            16 -> 5900
            17 -> 6700
            18 -> 7550
            19 -> 8450
            20 -> 9400
            21 -> 10400 
            else -> if (level > 21) 10400 else 0
        }
    }

    fun calculateLevelProgress(totalXp: Int, currentLevel: Int): Float {
        if (currentLevel >= 20) return 1.0f
        val currentLevelXp = xpRequiredForLevel(currentLevel)
        val nextLevelXp = xpRequiredForLevel(currentLevel + 1)
        val xpInCurrentLevel = totalXp - currentLevelXp
        val xpNeeded = nextLevelXp - currentLevelXp
        if (xpNeeded <= 0) return 1.0f
        return (xpInCurrentLevel.toFloat() / xpNeeded).coerceIn(0f, 1f)
    }

    fun getOneTimeExp(event: XpEvent): Int {
        return oneTimeExp[event] ?: 0
    }

    fun getAchievementMilestoneExp(totalUnlocked: Int): Int {
        return achievementMilestoneExp[totalUnlocked] ?: 0
    }

    fun getStageCompletionExp(stageId: Int): Int {
        return stageCompletionExp[stageId] ?: 0
    }

    fun calculateStreakBonus(streakCount: Int): Int {
        return when {
            streakCount <= 1 -> 0
            streakCount in 2..4 -> 5
            streakCount in 5..8 -> 10
            streakCount in 9..13 -> 15
            else -> 20 
        }
    }

    fun calculateTipe2Exp(expFull: Int, playCount: Int): Int {
        val percentage = when {
            playCount <= 1 -> 1.0f
            playCount == 2 -> 0.8f
            playCount == 3 -> 0.5f
            else -> 0.25f
        }
        return (expFull * percentage).toInt()
    }

    fun calculateRetryExp(expFull: Int, isFirstAttempt: Boolean): Int {
        return if (isFirstAttempt) expFull else (expFull * 0.8f).toInt()
    }

    fun buildFeedbackMessage(expEarned: Int, streakCount: Int): String {
        val streakBonus = calculateStreakBonus(streakCount)
        val lines = mutableListOf("+$expEarned EXP")
        if (streakBonus > 0) {
            lines.add("+$streakBonus EXP")
        }
        if (streakCount >= 2) {
            lines.add("Streak $streakCount")
        }
        return lines.joinToString("\n")
    }
}
