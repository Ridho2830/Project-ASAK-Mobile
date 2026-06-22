package com.unram.asakv2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unram.asakv2.data.local.entity.QuizSessionEntity
import com.unram.asakv2.data.local.entity.UserHurufProgressEntity

@Dao
interface StatisticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuizSession(session: QuizSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHurufProgress(progress: UserHurufProgressEntity): Long

    @Query("SELECT * FROM quiz_sessions WHERE isSynced = 0")
    fun getUnsyncedQuizSessions(): List<QuizSessionEntity>

    @Query("SELECT * FROM user_huruf_progress WHERE isSynced = 0")
    fun getUnsyncedHurufProgress(): List<UserHurufProgressEntity>

    @Query("UPDATE quiz_sessions SET isSynced = 1 WHERE id IN (:ids)")
    fun markQuizSessionsSynced(ids: List<String>): Int

    @Query("UPDATE user_huruf_progress SET isSynced = 1 WHERE id IN (:ids)")
    fun markHurufProgressSynced(ids: List<String>): Int
}
