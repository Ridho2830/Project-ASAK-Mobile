package com.unram.asakv2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unram.asakv2.model.BagianResult
import com.unram.asakv2.model.QuizPlan
import com.unram.asakv2.utils.XpCalculator

class QuizViewModel : ViewModel() {

    private var quizPlans: List<QuizPlan> = emptyList()
    private var currentIndex = 0
    private var totalExp = 0
    private var correctCount = 0
    private var currentStreak = 0
    private var maxStreak = 0

    private val _currentPlan = MutableLiveData<QuizPlan?>()
    val currentPlan: LiveData<QuizPlan?> = _currentPlan

    private val _isFinished = MutableLiveData<Boolean>(false)
    val isFinished: LiveData<Boolean> = _isFinished

    private val _bagianResult = MutableLiveData<BagianResult>()
    val bagianResult: LiveData<BagianResult> = _bagianResult

    fun loadBagian(stageId: Int, bagianId: Int, plans: List<QuizPlan>) {
        quizPlans = plans
        currentIndex = 0
        totalExp = 0
        correctCount = 0
        currentStreak = 0
        maxStreak = 0
        _isFinished.value = false
        _currentPlan.value = quizPlans.firstOrNull()
    }

    fun submitResult(expEarned: Int, isCorrect: Boolean) {
        val currentPlanIsStreak = quizPlans.getOrNull(currentIndex)?.isStreakMode ?: true

        totalExp += expEarned

        if (isCorrect) {
            correctCount++
            if (currentPlanIsStreak) {
                currentStreak++
                if (currentStreak > maxStreak) maxStreak = currentStreak
                val bonus = XpCalculator.calculateStreakBonus(currentStreak)
                totalExp += bonus
            }
        } else {
            if (currentPlanIsStreak) currentStreak = 0
        }

        moveToNext()
    }

    private fun moveToNext() {
        currentIndex++
        if (currentIndex >= quizPlans.size) {
            val result = BagianResult(
                totalExp = totalExp,
                correctCount = correctCount,
                totalCount = quizPlans.size,
                maxStreak = maxStreak
            )
            _bagianResult.value = result
            _isFinished.value = true
        } else {
            _currentPlan.value = quizPlans[currentIndex]
        }
    }

    fun getCurrentIndex(): Int = currentIndex
    fun getTotalPlans(): Int = quizPlans.size
    fun getCurrentStreak(): Int = currentStreak

    fun getNextStreakIfCorrect(): Int {
        val currentPlanIsStreak = quizPlans.getOrNull(currentIndex)?.isStreakMode ?: true
        return if (currentPlanIsStreak) currentStreak + 1 else 0
    }
}