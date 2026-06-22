package com.unram.asakv2.model

/**
 * Hasil satu sesi bagian quiz (dipakai QuizViewModel → QuizResultFragment).
 */
data class BagianResult(
    val totalExp: Int,
    val correctCount: Int,
    val totalCount: Int,
    val maxStreak: Int
) {
    val accuracy: Float get() = if (totalCount == 0) 0f else correctCount.toFloat() / totalCount
    val isPassed: Boolean get() = accuracy >= 0.75f
}

/**
 * Metadata tiap sub-stage di HomeFragment.
 * Memetakan buttonId XML ke stageNumber, subStageNumber, themeType, dan warna stage.
 */
data class SubStageProgress(
    val buttonId: Int,
    val stageNumber: Int,
    val subStageNumber: Int,
    val themeType: String,      // "BUKU", "KAMERA", "PUZZLE"
    val stageColorRes: Int
)