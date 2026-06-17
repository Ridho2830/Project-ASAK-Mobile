package com.unram.asakv2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unram.asakv2.model.UserAchievement

/**
 * AchievementViewModel — Load achievement, pilih display di profil.
 * Mengelola daftar achievement dan memilih yang ditampilkan di profil.
 * [DESTI]
 */
class AchievementViewModel : ViewModel() {

    private val _achievements = MutableLiveData<List<UserAchievement>>()
    val achievements: LiveData<List<UserAchievement>> = _achievements

    private val _selectedAchievement = MutableLiveData<UserAchievement?>()
    val selectedAchievement: LiveData<UserAchievement?> = _selectedAchievement

    fun loadAchievements(userId: String) {
        // TODO: Load daftar achievement user dari AchievementRepository
    }

    fun selectDisplayAchievement(achievement: UserAchievement) {
        _selectedAchievement.value = achievement
        // TODO: Update display achievement di Firestore
    }
}
