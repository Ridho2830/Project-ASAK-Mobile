package com.unram.asakv2.repository

import com.unram.asakv2.model.QuizSession

class QuizRepository {

    fun saveQuizSession(session: QuizSession, callback: (Result<Boolean>) -> Unit) {
        
    }

    fun getQuizHistory(userId: String, callback: (Result<List<QuizSession>>) -> Unit) {
        
    }

    fun getAccuracyPerHuruf(userId: String, callback: (Result<Map<String, Float>>) -> Unit) {
        
    }
}
