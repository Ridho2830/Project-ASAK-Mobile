package com.unram.asakv2.data.repository

import com.unram.asakv2.data.remote.ApiService
import com.unram.asakv2.data.remote.LeaderboardResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LeaderboardRepository(
    private val apiService: ApiService
) {
    fun getWeeklyLeaderboard(league: String): Flow<Result<LeaderboardResponse>> = flow {
        try {
            val response = apiService.getWeeklyLeaderboard(league)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch leaderboard: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
