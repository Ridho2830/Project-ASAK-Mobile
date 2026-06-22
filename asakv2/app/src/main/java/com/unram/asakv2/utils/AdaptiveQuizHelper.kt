package com.unram.asakv2.utils

import com.unram.asakv2.model.AksaraHuruf
import com.unram.asakv2.model.UserHurufProgress

/**
 * AdaptiveQuizHelper — Pilih soal berdasar akurasi per huruf.
 * Memilih huruf untuk quiz berdasarkan akurasi rendah dan spaced repetition.
 * [RENDI]
 */
object AdaptiveQuizHelper {

    /**
     * Pilih huruf untuk quiz berdasarkan adaptive logic.
     * Prioritas: huruf dengan akurasi rendah + huruf yang sudah waktunya di-review.
     */
    fun selectHurufForQuiz(
        allHuruf: List<AksaraHuruf>,
        progressList: List<UserHurufProgress>,
        count: Int = Constants.TOTAL_QUIZ_QUESTIONS
    ): List<AksaraHuruf> {
        // TODO: Implementasi adaptive selection
        // 1. Prioritaskan huruf dengan akurasi < 70%
        // 2. Tambahkan huruf yang sudah due for review (SM-2)
        // 3. Sisanya random dari huruf yang tersedia
        return allHuruf.shuffled().take(count)
    }

    /**
     * Pilih tipe quiz berdasarkan akurasi huruf.
     * Huruf dengan akurasi rendah mendapat tipe soal yang lebih mudah (Tipe 1, 2).
     * Huruf dengan akurasi tinggi mendapat tipe soal yang lebih sulit (Tipe 3, 4, 5).
     */
    fun selectQuizType(accuracy: Float): Int {
        return when {
            accuracy < 0.3f -> 1  // Sangat rendah → Gambar pilih nama
            accuracy < 0.5f -> 2  // Rendah → Audio pilih gambar
            accuracy < 0.7f -> (1..3).random() // Sedang → Tipe 1-3
            accuracy < 0.9f -> (3..4).random() // Tinggi → Tipe 3-4
            else -> (3..5).random()             // Sangat tinggi → Tipe 3-5
        }
    }
}
