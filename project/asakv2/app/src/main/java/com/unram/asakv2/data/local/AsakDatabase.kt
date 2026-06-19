package com.unram.asakv2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unram.asakv2.data.local.dao.StatisticDao
import com.unram.asakv2.data.local.dao.UserDao
import com.unram.asakv2.data.local.entity.QuizSessionEntity
import com.unram.asakv2.data.local.entity.UserEntity
import com.unram.asakv2.data.local.entity.UserHurufProgressEntity

@Database(
    entities = [
        UserEntity::class,
        QuizSessionEntity::class,
        UserHurufProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AsakDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun statisticDao(): StatisticDao

    companion object {
        @Volatile
        private var INSTANCE: AsakDatabase? = null

        fun getDatabase(context: Context): AsakDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AsakDatabase::class.java,
                    "asak_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
