package com.unram.asakv2.repository

import com.unram.asakv2.model.QuizSession

/**
 * QuizRepository — Simpan sesi quiz, history akurasi.
 * Mengelola penyimpanan sesi quiz dan riwayat akurasi per huruf.
 * [RENDI]
 */
class QuizRepository {

    fun saveQuizSession(session: QuizSession, callback: (Result<Boolean>) -> Unit) {
        // TODO: Simpan sesi quiz ke Firestore
    }

    fun getQuizHistory(userId: String, callback: (Result<List<QuizSession>>) -> Unit) {
        // TODO: Load riwayat quiz user dari Firestore
    }

    fun getAccuracyPerHuruf(userId: String, callback: (Result<Map<String, Float>>) -> Unit) {
        // TODO: Hitung akurasi per huruf dari history quiz
    }
}
