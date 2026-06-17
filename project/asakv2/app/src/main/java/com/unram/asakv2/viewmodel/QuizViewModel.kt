package com.unram.asakv2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unram.asakv2.model.QuizSession

/**
 * QuizViewModel — Adaptive quiz logic, scoring, XP award.
 * Mengelola logika quiz adaptif, perhitungan skor, dan pemberian XP.
 * [RENDI]
 */
class QuizViewModel : ViewModel() {

    private val _currentQuizType = MutableLiveData<Int>()
    val currentQuizType: LiveData<Int> = _currentQuizType

    private val _currentQuestionIndex = MutableLiveData<Int>()
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _quizSession = MutableLiveData<QuizSession?>()
    val quizSession: LiveData<QuizSession?> = _quizSession

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _xpEarned = MutableLiveData<Int>()
    val xpEarned: LiveData<Int> = _xpEarned

    fun startQuiz(userId: String) {
        // TODO: Mulai sesi quiz baru menggunakan AdaptiveQuizHelper
    }

    fun submitAnswer(answer: String, isCorrect: Boolean) {
        // TODO: Simpan jawaban dan update skor
    }

    fun nextQuestion() {
        // TODO: Load soal berikutnya berdasarkan adaptive logic
    }

    fun finishQuiz() {
        // TODO: Hitung skor akhir, XP, dan simpan ke QuizRepository
    }
}
