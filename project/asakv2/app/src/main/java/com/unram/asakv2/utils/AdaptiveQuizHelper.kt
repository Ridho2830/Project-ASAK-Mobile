package com.unram.asakv2.utils

import com.unram.asakv2.model.AksaraHuruf
import com.unram.asakv2.model.UserHurufProgress

object AdaptiveQuizHelper {

    fun selectHurufForQuiz(
        allHuruf: List<AksaraHuruf>,
        progressList: List<UserHurufProgress>,
        count: Int = Constants.TOTAL_QUIZ_QUESTIONS
    ): List<AksaraHuruf> {
        
        return allHuruf.shuffled().take(count)
    }

    fun selectQuizType(accuracy: Float): Int {
        return when {
            accuracy < 0.3f -> 1  
            accuracy < 0.5f -> 2  
            accuracy < 0.7f -> (1..3).random() 
            accuracy < 0.9f -> (3..4).random() 
            else -> (3..5).random()             
        }
    }
}
