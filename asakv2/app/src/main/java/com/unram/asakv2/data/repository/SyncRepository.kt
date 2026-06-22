package com.unram.asakv2.data.repository

import com.unram.asakv2.data.local.dao.StatisticDao
import com.unram.asakv2.data.remote.ApiService
import com.unram.asakv2.data.remote.SyncRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepository(
    private val apiService: ApiService,
    private val statisticDao: StatisticDao
) {
    suspend fun syncOfflineData(userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val unsyncedSessions = statisticDao.getUnsyncedQuizSessions()
            val unsyncedProgress = statisticDao.getUnsyncedHurufProgress()

            if (unsyncedSessions.isEmpty() && unsyncedProgress.isEmpty()) {
                return@withContext Result.success(true) 
            }

            val request = SyncRequest(
                user_id = userId,
                quiz_sessions = unsyncedSessions,
                huruf_progress = unsyncedProgress
            )

            val response = apiService.syncData(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                if (unsyncedSessions.isNotEmpty()) {
                    statisticDao.markQuizSessionsSynced(unsyncedSessions.map { it.id })
                }
                if (unsyncedProgress.isNotEmpty()) {
                    statisticDao.markHurufProgressSynced(unsyncedProgress.map { it.id })
                }
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Sync failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
