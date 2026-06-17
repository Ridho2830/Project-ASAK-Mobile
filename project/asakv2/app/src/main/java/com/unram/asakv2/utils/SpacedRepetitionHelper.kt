package com.unram.asakv2.utils

import com.unram.asakv2.model.UserHurufProgress

/**
 * SpacedRepetitionHelper — Hitung next_review_at (SM-2).
 * Implementasi algoritma SM-2 untuk menentukan waktu review berikutnya.
 * [RENDI]
 */
object SpacedRepetitionHelper {

    /**
     * Hitung parameter SM-2 yang baru berdasarkan kualitas jawaban.
     * @param progress Progress huruf saat ini
     * @param quality Kualitas jawaban (0-5). 0 = salah total, 5 = benar sempurna
     * @return UserHurufProgress yang sudah diupdate
     */
    fun calculateNextReview(progress: UserHurufProgress, quality: Int): UserHurufProgress {
        val q = quality.coerceIn(0, 5)

        // Hitung ease factor baru
        var newEaseFactor = progress.easeFactor + (0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f))
        newEaseFactor = newEaseFactor.coerceAtLeast(1.3f)

        // Hitung interval baru
        val newInterval = when {
            q < 3 -> 1 // Reset jika kualitas rendah
            progress.interval == 1 -> 6
            else -> (progress.interval * newEaseFactor).toInt()
        }

        // Hitung next review time
        val nextReviewAt = System.currentTimeMillis() + (newInterval * 24 * 60 * 60 * 1000L)

        return progress.copy(
            easeFactor = newEaseFactor,
            interval = newInterval,
            nextReviewAt = nextReviewAt,
            lastAttemptAt = System.currentTimeMillis()
        )
    }

    /**
     * Cek apakah huruf sudah waktunya di-review.
     */
    fun isDueForReview(progress: UserHurufProgress): Boolean {
        return System.currentTimeMillis() >= progress.nextReviewAt
    }
}
