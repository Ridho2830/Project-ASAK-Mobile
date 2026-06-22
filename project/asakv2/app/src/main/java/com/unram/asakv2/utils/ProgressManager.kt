package com.unram.asakv2.utils

import android.content.Context
import android.content.SharedPreferences

object ProgressManager {

    private const val PREF_NAME  = "asak_progress"
    private const val KEY_STAGE  = "current_stage"
    private const val KEY_BAGIAN = "current_bagian"

    // Prefix key untuk status AR sub-stage per stage
    private const val KEY_AR_PREFIX = "ar_completed_stage_"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getCurrentStage(context: Context): Int =
        prefs(context).getInt(KEY_STAGE, 1)

    fun getCurrentBagian(context: Context): Int =
        prefs(context).getInt(KEY_BAGIAN, 1)

    /**
     * Maju ke bagian/stage berikutnya.
     * Hanya dipanggil jika user LULUS (accuracy >= 75%).
     * Mengembalikan progress terbaru (nextStage, nextBagian).
     */
    fun advanceProgress(context: Context, stageId: Int, bagianId: Int): Pair<Int, Int> {
        val totalBagian   = com.unram.asakv2.quiz.QuizScenario.allStages[stageId]?.size ?: return Pair(stageId, bagianId)
        val currentStage  = getCurrentStage(context)
        val currentBagian = getCurrentBagian(context)

        // Hanya maju jika yang diselesaikan adalah bagian aktif
        if (stageId != currentStage || bagianId != currentBagian) return Pair(currentStage, currentBagian)

        val (nextStage, nextBagian) = if (bagianId >= totalBagian) {
            val ns = stageId + 1
            if (com.unram.asakv2.quiz.QuizScenario.allStages.containsKey(ns)) Pair(ns, 1)
            else Pair(stageId, bagianId)
        } else {
            Pair(stageId, bagianId + 1)
        }

        prefs(context).edit()
            .putInt(KEY_STAGE,  nextStage)
            .putInt(KEY_BAGIAN, nextBagian)
            .apply()

        return Pair(nextStage, nextBagian)
    }

    /** Sinkronisasi progress lokal dari data server (Firestore) */
    fun syncLocalProgress(context: Context, serverStage: Int, serverBagian: Int) {
        val localStage = getCurrentStage(context)
        val localBagian = getCurrentBagian(context)
        val isServerAhead = serverStage > localStage || (serverStage == localStage && serverBagian > localBagian)
        if (isServerAhead) {
            prefs(context).edit()
                .putInt(KEY_STAGE, serverStage)
                .putInt(KEY_BAGIAN, serverBagian)
                .apply()
        }
    }

    /** Simpan status AR sub-stage tertentu sudah selesai. */
    fun setArCompleted(context: Context, stageId: Int) {
        prefs(context).edit()
            .putBoolean("$KEY_AR_PREFIX$stageId", true)
            .apply()
    }

    /** Cek apakah AR sub-stage stageId sudah selesai. */
    fun isArCompleted(context: Context, stageId: Int): Boolean =
        prefs(context).getBoolean("$KEY_AR_PREFIX$stageId", false)

    fun isStageCompleted(context: Context, stageId: Int): Boolean =
        getCurrentStage(context) > stageId

    fun isStagePassed(context: Context, stageId: Int): Boolean {
        val currentStage = getCurrentStage(context)
        val currentBagian = getCurrentBagian(context)
        return currentStage > stageId || (currentStage == stageId && currentBagian > 4)
    }

    /** Reset progress ke awal (untuk testing). */
    fun resetProgress(context: Context) {
        val editor = prefs(context).edit()
        editor.putInt(KEY_STAGE, 1)
        editor.putInt(KEY_BAGIAN, 1)
        com.unram.asakv2.quiz.QuizScenario.allStages.keys.forEach { id ->
            editor.remove("$KEY_AR_PREFIX$id")
        }
        editor.apply()
    }
}