package com.unram.asakv2.data.remote

import com.unram.asakv2.data.local.entity.QuizSessionEntity
import com.unram.asakv2.data.local.entity.UserHurufProgressEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class SyncRequest(
    val user_id: String,
    val quiz_sessions: List<QuizSessionEntity>,
    val huruf_progress: List<UserHurufProgressEntity>
)

data class SyncResponse(
    val status: String,
    val message: String
)

data class LeaderboardResponse(
    val status: String,
    val data: LeaderboardData
)

data class LeaderboardData(
    val league_name: String,
    val week_id: String,
    val reset_in_seconds: Long,
    val rankings: List<RankingItem>
)

data class RankingItem(
    val user_id: String,
    val username: String,
    val league_name: String,
    val total_xp: Long,
    val rank_position: Int
)

data class StatisticResponse(
    val status: String,
    val data: StatisticData
)

data class StatisticData(
    val total_xp: Long,
    val current_streak: Long,
    val highest_streak: Long,
    val total_sessions: Int,
    val avg_accuracy: Double,
    val weekly_xp_chart: List<ChartData>
)

data class ChartData(
    val day: String,
    val xp: Long
)

interface ApiService {
    @POST("sync")
    suspend fun syncData(@Body request: SyncRequest): Response<SyncResponse>

    @GET("leaderboard/weekly")
    suspend fun getWeeklyLeaderboard(@Query("league") league: String = "Bronze"): Response<LeaderboardResponse>

    @GET("users/{user_id}/statistics")
    suspend fun getUserStatistics(@Path("user_id") userId: String): Response<StatisticResponse>
}
