package com.unram.asakv2.utils

import com.unram.asakv2.model.UserHurufProgress

object SpacedRepetitionHelper {

    fun calculateNextReview(progress: UserHurufProgress, quality: Int): UserHurufProgress {
        val q = quality.coerceIn(0, 5)

        var newEaseFactor = progress.easeFactor + (0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f))
        newEaseFactor = newEaseFactor.coerceAtLeast(1.3f)

        val newInterval = when {
            q < 3 -> 1 
            progress.interval == 1 -> 6
            else -> (progress.interval * newEaseFactor).toInt()
        }

        val nextReviewAt = System.currentTimeMillis() + (newInterval * 24 * 60 * 60 * 1000L)

        return progress.copy(
            easeFactor = newEaseFactor,
            interval = newInterval,
            nextReviewAt = nextReviewAt,
            lastAttemptAt = System.currentTimeMillis()
        )
    }

    fun isDueForReview(progress: UserHurufProgress): Boolean {
        return System.currentTimeMillis() >= progress.nextReviewAt
    }
}
