package com.unram.asakv2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * HomeViewModel — XP, streak, level progress.
 * Mengelola data beranda pengguna: total XP, streak harian, dan progress level.
 * [KANDA]
 */
class HomeViewModel : ViewModel() {

    private val _totalXp = MutableLiveData<Int>()
    val totalXp: LiveData<Int> = _totalXp

    private val _streak = MutableLiveData<Int>()
    val streak: LiveData<Int> = _streak

    private val _currentLevel = MutableLiveData<Int>()
    val currentLevel: LiveData<Int> = _currentLevel

    private val _levelProgress = MutableLiveData<Float>()
    val levelProgress: LiveData<Float> = _levelProgress

    fun loadHomeData(userId: String) {
        // TODO: Load XP, streak, level progress dari Firestore
    }
}
