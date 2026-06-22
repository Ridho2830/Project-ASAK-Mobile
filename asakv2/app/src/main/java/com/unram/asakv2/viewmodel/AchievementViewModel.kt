package com.unram.asakv2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unram.asakv2.model.UserAchievement
import com.unram.asakv2.repository.AchievementRepository

/**
 * AchievementViewModel — Mengelola list achievement dan pemilahan achievement terpilih untuk profil.
 */
class AchievementViewModel : ViewModel() {

    private val achievementRepository = AchievementRepository()

    private val _achievements = MutableLiveData<List<UserAchievement>>()
    val achievements: LiveData<List<UserAchievement>> = _achievements

    private val _selectedAchievements = MutableLiveData<List<UserAchievement?>>()
    val selectedAchievements: LiveData<List<UserAchievement?>> = _selectedAchievements

    private val _updateResult = MutableLiveData<Result<Boolean>>()
    val updateResult: LiveData<Result<Boolean>> = _updateResult

    fun loadAchievements(userId: String, selectedIds: List<String>) {
        achievementRepository.getUserAchievements(userId) { result ->
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                _achievements.postValue(list)
                
                // Presisi index slot: map selectedIds ke UserAchievement atau null
                val selected = selectedIds.map { id ->
                    list.find { it.achievementId == id }
                }
                _selectedAchievements.postValue(selected)
            }
        }
    }

    fun updateSelectedAchievements(userId: String, selectedIds: List<String>) {
        achievementRepository.updateSelectedAchievements(userId, selectedIds) { result ->
            _updateResult.postValue(result)
            if (result.isSuccess) {
                loadAchievements(userId, selectedIds)
            }
        }
    }
}
