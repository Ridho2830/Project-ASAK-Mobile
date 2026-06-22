package com.unram.asakv2.achievement

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Singleton yang menampung antrian dialog "selamat unlock achievement".
 * Dipanggil dari mana saja (ViewModel/Fragment) ketika addExp mengembalikan true.
 * Fragment quiz TIDAK boleh observe — cek isInQuiz sebelum dispatch.
 */
object AchievementUnlockManager {

    private val _pendingUnlock = MutableLiveData<String?>()
    val pendingUnlock: LiveData<String?> = _pendingUnlock

    private val queue: ArrayDeque<String> = ArrayDeque()
    private var isShowing = false
    private val handler = Handler(Looper.getMainLooper())

    var isInQuiz: Boolean = false

    fun enqueue(achievementId: String) {
        queue.addLast(achievementId)
        tryDispatch()
    }

    fun onDialogDismissed() {
        isShowing = false
        _pendingUnlock.value = null
        handler.postDelayed({ tryDispatch() }, 500)
    }

    private fun tryDispatch() {
        if (isShowing || isInQuiz || queue.isEmpty()) return
        handler.postDelayed({
            if (isInQuiz || queue.isEmpty()) return@postDelayed
            isShowing = true
            _pendingUnlock.value = queue.removeFirst()
        }, 2000)
    }
}
