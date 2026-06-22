package com.unram.asakv2.repository

import com.unram.asakv2.firebase.FirestoreHelper
import com.unram.asakv2.model.User

class UserRepository {

    private val achievementRepository = AchievementRepository()

    fun createUserIfNotExists(user: User, callback: (Result<Boolean>) -> Unit) {
        val userRef = FirestoreHelper.usersCollection().document(user.uid)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                
                achievementRepository.seedAchievementsIfEmpty()

                userRef.set(user).addOnSuccessListener {
                    achievementRepository.checkAndTriggerAchievements(user.uid, "daftar") { result ->
                        if (result.isSuccess) {
                            callback(Result.success(true))
                        } else {
                            callback(Result.failure(result.exceptionOrNull() ?: Exception("Seeding newbie failed")))
                        }
                    }
                }.addOnFailureListener { e ->
                    callback(Result.failure(e))
                }
            } else {
                
                achievementRepository.seedAchievementsIfEmpty()
                callback(Result.success(true))
            }
        }.addOnFailureListener { e ->
            callback(Result.failure(e))
        }
    }

    fun getUser(userId: String, callback: (Result<User?>) -> Unit) {
        FirestoreHelper.usersCollection().document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(Result.success(user))
                } else {
                    callback(Result.success(null))
                }
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }

    fun updateUserName(userId: String, newName: String, callback: (Result<Boolean>) -> Unit) {
        achievementRepository.checkAndTriggerAchievements(userId, "ubah_nama", customUpdate = { user ->
            user.copy(name = newName)
        }) { result ->
            if (result.isSuccess) {
                callback(Result.success(true))
            } else {
                callback(Result.failure(result.exceptionOrNull() ?: Exception("Gagal ubah nama")))
            }
        }
    }

    fun updateUserTagline(userId: String, newTagline: String, callback: (Result<Boolean>) -> Unit) {
        achievementRepository.checkAndTriggerAchievements(userId, "ubah_tagline", customUpdate = { user ->
            user.copy(tagline = newTagline)
        }) { result ->
            if (result.isSuccess) {
                callback(Result.success(true))
            } else {
                callback(Result.failure(result.exceptionOrNull() ?: Exception("Gagal ubah tagline")))
            }
        }
    }

    fun updateUserPhoto(userId: String, photoUrl: String, callback: (Result<Boolean>) -> Unit) {
        achievementRepository.checkAndTriggerAchievements(userId, "ubah_foto", customUpdate = { user ->
            user.copy(photoUrl = photoUrl)
        }) { result ->
            if (result.isSuccess) {
                callback(Result.success(true))
            } else {
                callback(Result.failure(result.exceptionOrNull() ?: Exception("Gagal ubah foto")))
            }
        }
    }

    fun updateQuizResult(
        userId: String,
        xpGained: Int,
        accuracy: Double,
        isWriting: Boolean,
        isSpeaking: Boolean,
        streak: Int,
        completedStageId: Int,
        currentStage: Int,
        currentBagian: Int,
        callback: (Result<Boolean>) -> Unit
    ) {
        achievementRepository.checkAndTriggerAchievements(userId, "belajar", customUpdate = { user ->
            val writingHistory = user.writingAccuracyHistory.toMutableList()
            val speakingHistory = user.speakingAccuracyHistory.toMutableList()
            val stages = user.completedStages.toMutableList()
            
            if (isWriting) writingHistory.add(accuracy)
            if (isSpeaking) speakingHistory.add(accuracy)
            
            if (completedStageId > 0 && !stages.contains(completedStageId)) {
                stages.add(completedStageId)
            }
            
            val updatedStreak = maxOf(user.streak, streak)
            val updatedMaxStreak = maxOf(user.maxStreak, streak)

            val updatedXp = user.xp + xpGained
            var newLevel = user.level
            while (newLevel < 21 && updatedXp >= com.unram.asakv2.utils.XpCalculator.xpRequiredForLevel(newLevel + 1)) {
                newLevel++
            }

            user.copy(
                xp = updatedXp,
                level = newLevel,
                currentStage = currentStage,
                currentBagian = currentBagian,
                writingAccuracyHistory = writingHistory,
                speakingAccuracyHistory = speakingHistory,
                completedStages = stages,
                streak = updatedStreak,
                maxStreak = updatedMaxStreak
            )
        }) { result ->
            if (result.isSuccess) {
                callback(Result.success(true))
            } else {
                callback(Result.failure(result.exceptionOrNull() ?: Exception("Gagal update hasil")))
            }
        }
    }

    fun updateStreak(userId: String, newStreak: Int, callback: (Result<Boolean>) -> Unit) {
        achievementRepository.checkAndTriggerAchievements(userId, null, customUpdate = { user ->
            user.copy(
                streak = newStreak,
                maxStreak = maxOf(user.maxStreak, newStreak)
            )
        }) { result ->
            if (result.isSuccess) {
                callback(Result.success(true))
            } else {
                callback(Result.failure(result.exceptionOrNull() ?: Exception("Gagal update streak")))
            }
        }
    }

    fun resetUserProgress(userId: String, callback: (Result<Boolean>) -> Unit) {
        val userRef = FirestoreHelper.usersCollection().document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val rawUser = document.toObject(User::class.java) ?: User(uid = userId)
                val resetUser = rawUser.copy(
                    xp = 0,
                    level = 1,
                    currentStage = 1,
                    currentBagian = 1,
                    streak = 0,
                    maxStreak = 0,
                    completedStages = emptyList(),
                    unlockedLetters = emptyList(),
                    unlockedWisata = emptyList(),
                    unlockedBudaya = emptyList(),
                    writingAccuracyHistory = emptyList(),
                    speakingAccuracyHistory = emptyList(),
                    earnedOneTimeXpEvents = emptyList(),
                    unlockedAchievements = emptyList(),
                    earnedMilestoneRewards = emptyList()
                )
                
                val userAcvRef = FirestoreHelper.userAchievementsCollection(userId)
                userAcvRef.get().addOnSuccessListener { snapshot ->
                    val batch = FirestoreHelper.db.batch()
                    for (doc in snapshot.documents) {
                        batch.delete(doc.reference)
                    }
                    batch.set(userRef, resetUser)
                    batch.commit().addOnSuccessListener {
                        callback(Result.success(true))
                    }.addOnFailureListener { e ->
                        callback(Result.failure(e))
                    }
                }.addOnFailureListener { e ->
                    userRef.set(resetUser).addOnSuccessListener {
                        callback(Result.success(true))
                    }.addOnFailureListener { err ->
                        callback(Result.failure(err))
                    }
                }
            } else {
                callback(Result.failure(Exception("User does not exist")))
            }
        }.addOnFailureListener { e ->
            callback(Result.failure(e))
        }
    }
}
