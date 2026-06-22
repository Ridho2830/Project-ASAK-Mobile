package com.unram.asakv2.model

data class BagianResult(
    val totalExp: Int,
    val correctCount: Int,
    val totalCount: Int,
    val maxStreak: Int
) {
    val accuracy: Float get() = if (totalCount == 0) 0f else correctCount.toFloat() / totalCount
    val isPassed: Boolean get() = accuracy >= 0.75f
}

data class SubStageProgress(
    val buttonId: Int,
    val stageNumber: Int,
    val subStageNumber: Int,
    val themeType: String,      
    val stageColorRes: Int
)