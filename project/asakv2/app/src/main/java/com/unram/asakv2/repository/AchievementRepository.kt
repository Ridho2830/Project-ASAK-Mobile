package com.unram.asakv2.repository

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.unram.asakv2.firebase.FirestoreHelper
import com.unram.asakv2.model.Achievement
import com.unram.asakv2.model.User
import com.unram.asakv2.model.UserAchievement
import com.unram.asakv2.utils.XpCalculator

class AchievementRepository {

    companion object {
        private const val TAG = "AchievementRepo"

        val TEMPLATE_ACHIEVEMENTS = listOf(
            Achievement("newbie", "Newbie", "Daftar pertama kali di ASAK", "", "general", 1, "daftar"),
            Achievement("ubah_nama", "Siapa Aku?", "Mengubah nama profil pertama kali", "", "profile", 1, "ubah_nama"),
            Achievement("ubah_tagline", "Tagline Keren", "Mengubah tagline profil pertama kali", "", "profile", 1, "ubah_tagline"),
            Achievement("ubah_foto", "Wajah Baru", "Mengubah foto profil pertama kali", "", "profile", 1, "ubah_foto"),
            Achievement("ubah_achievement", "Pamer Badge", "Mengubah pajangan achievement di profil", "", "profile", 1, "ubah_achievement"),
            Achievement("penggunaan_ar", "Arsitek Virtual", "Menggunakan fitur scan AR pertama kali", "", "ar", 1, "penggunaan_ar"),
            Achievement("belajar_pertama", "Langkah Awal", "Memulai belajar aksara pertama kali", "", "study", 1, "belajar_pertama"),
            Achievement("buka_semua_huruf", "Kolektor Aksara", "Membuka semua 18 huruf aksara", "", "milestone", 18, "buka_semua_huruf"),
            Achievement("buka_semua_wisata", "Penjelajah Lombok", "Membuka semua 8 tempat wisata", "", "milestone", 8, "buka_semua_wisata"),
            Achievement("buka_semua_budaya", "Pecinta Budaya", "Membuka semua 12 kesenian/budaya", "", "milestone", 12, "buka_semua_budaya"),
            Achievement("selesai_stage_6", "Tamat", "Menyelesaikan seluruh materi hingga stage 6", "", "milestone", 6, "selesai_stage_6"),
            Achievement("akurasi_menulis_90", "Kaligrafer", "Mencapai akurasi menulis >= 90% pertama kali", "", "accuracy", 90, "akurasi_menulis_90"),
            Achievement("akurasi_mengucapkan_90", "Fasih Sasak", "Mencapai akurasi mengucapkan >= 90% pertama kali", "", "accuracy", 90, "akurasi_mengucapkan_90"),
            Achievement("naik_level_2", "Langkah Kedua", "Naik level pertama kali (mencapai Level 2)", "", "level", 2, "naik_level_2"),
            Achievement("mencapai_level_5", "Pelajar Handal", "Mencapai Level 5", "", "level", 5, "mencapai_level_5"),
            Achievement("mencapai_level_10", "Pendekar Aksara", "Mencapai Level 10", "", "level", 10, "mencapai_level_10"),
            Achievement("mencapai_level_15", "Resi Sasak", "Mencapai Level 15", "", "level", 15, "mencapai_level_15"),
            Achievement("level_max", "Dewa Aksara", "Mencapai level maksimal (Level 20)", "", "level", 20, "level_max"),
            Achievement("streak_5", "Tekun", "Mencapai streak belajar 5 hari berturut-turut", "", "streak", 5, "streak_5"),
            Achievement("unlock_24_achievement", "Pemburu Badge", "Membuka 24 achievement lainnya", "", "milestone", 24, "unlock_24_achievement"),
            
            Achievement("acv21", "Belajar Ha-Na-Ca-Ra", "Menyelesaikan Stage 1", "", "stage", 1, "stage_1"),
            Achievement("acv22", "Belajar Ka-Da-Ta-Sa", "Menyelesaikan Stage 2", "", "stage", 2, "stage_2"),
            Achievement("acv23", "Belajar Wa-La-Ma-Ga", "Menyelesaikan Stage 3", "", "stage", 3, "stage_3"),
            Achievement("acv24", "Belajar Ba-Nga-Pa", "Menyelesaikan Stage 4", "", "stage", 4, "stage_4"),
            Achievement("acv25", "Belajar Ja-Ya-Nya", "Menyelesaikan Stage 5", "", "stage", 5, "stage_5")
        )
    }

    fun seedAchievementsIfEmpty() {
        FirestoreHelper.achievementsCollection().get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Achievements collection is empty. Seeding templates...")
                    val batch = FirestoreHelper.db.batch()
                    for (tmpl in TEMPLATE_ACHIEVEMENTS) {
                        val docRef = FirestoreHelper.achievementsCollection().document(tmpl.id)
                        batch.set(docRef, tmpl)
                    }
                    batch.commit()
                        .addOnSuccessListener { Log.d(TAG, "Achievements seeded successfully.") }
                        .addOnFailureListener { e -> Log.e(TAG, "Failed seeding achievements", e) }
                } else {
                    Log.d(TAG, "Achievements already exist. Checking if we need to add missing ones...")
                    val existingIds = snapshot.toObjects(Achievement::class.java).map { it.id }.toSet()
                    val batch = FirestoreHelper.db.batch()
                    var addedCount = 0
                    for (tmpl in TEMPLATE_ACHIEVEMENTS) {
                        if (!existingIds.contains(tmpl.id)) {
                            val docRef = FirestoreHelper.achievementsCollection().document(tmpl.id)
                            batch.set(docRef, tmpl)
                            addedCount++
                        }
                    }
                    if (addedCount > 0) {
                        batch.commit()
                            .addOnSuccessListener { Log.d(TAG, "Added $addedCount missing achievements.") }
                            .addOnFailureListener { e -> Log.e(TAG, "Failed adding missing achievements", e) }
                    }
                }
            }
    }

    fun getAllAchievements(callback: (Result<List<Achievement>>) -> Unit) {
        FirestoreHelper.achievementsCollection().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Achievement::class.java)
                if (list.isEmpty()) {
                    
                    callback(Result.success(TEMPLATE_ACHIEVEMENTS))
                } else {
                    callback(Result.success(list))
                }
            }
            .addOnFailureListener { e ->
                
                callback(Result.success(TEMPLATE_ACHIEVEMENTS))
            }
    }

    private fun getProgressForAchievement(id: String, user: User, isUnlocked: Boolean, requirement: Int): Int {
        if (isUnlocked) {
            return requirement
        }
        return when (id) {
            "buka_semua_huruf" -> user.unlockedLetters.size
            "buka_semua_wisata" -> user.unlockedWisata.size
            "buka_semua_budaya" -> user.unlockedBudaya.size
            "selesai_stage_6" -> user.completedStages.size
            "naik_level_2" -> user.level.coerceAtMost(2)
            "mencapai_level_5" -> user.level.coerceAtMost(5)
            "mencapai_level_10" -> user.level.coerceAtMost(10)
            "mencapai_level_15" -> user.level.coerceAtMost(15)
            "level_max" -> user.level.coerceAtMost(20)
            "streak_5" -> user.streak.coerceAtMost(5)
            "unlock_24_achievement" -> user.unlockedAchievements.size
            "akurasi_menulis_90" -> user.writingAccuracyHistory.maxOrNull()?.toInt() ?: 0
            "akurasi_mengucapkan_90" -> user.speakingAccuracyHistory.maxOrNull()?.toInt() ?: 0
            "acv21" -> if (user.completedStages.contains(1)) 1 else 0
            "acv22" -> if (user.completedStages.contains(2)) 1 else 0
            "acv23" -> if (user.completedStages.contains(3)) 1 else 0
            "acv24" -> if (user.completedStages.contains(4)) 1 else 0
            "acv25" -> if (user.completedStages.contains(5)) 1 else 0
            "newbie" -> if (user.unlockedAchievements.contains("newbie")) 1 else 0
            "ubah_nama" -> if (user.unlockedAchievements.contains("ubah_nama")) 1 else 0
            "ubah_tagline" -> if (user.unlockedAchievements.contains("ubah_tagline")) 1 else 0
            "ubah_foto" -> if (user.unlockedAchievements.contains("ubah_foto")) 1 else 0
            "ubah_achievement" -> if (user.unlockedAchievements.contains("ubah_achievement")) 1 else 0
            "penggunaan_ar" -> if (user.unlockedAchievements.contains("penggunaan_ar")) 1 else 0
            "belajar_pertama" -> if (user.unlockedAchievements.contains("belajar_pertama")) 1 else 0
            else -> 0
        }
    }

    fun getUserAchievements(userId: String, callback: (Result<List<UserAchievement>>) -> Unit) {
        getAllAchievements { templateResult ->
            if (templateResult.isFailure) {
                callback(Result.failure(templateResult.exceptionOrNull() ?: Exception("Gagal memuat template")))
                return@getAllAchievements
            }
            val templates = templateResult.getOrDefault(emptyList())

            FirestoreHelper.usersCollection().document(userId).get()
                .addOnSuccessListener { userDoc ->
                    val user = userDoc.toObject(User::class.java) ?: User(uid = userId)

                    FirestoreHelper.userAchievementsCollection(userId).get()
                        .addOnSuccessListener { snapshot ->
                            val userAchievementsMap = snapshot.toObjects(UserAchievement::class.java)
                                .associateBy { it.achievementId }

                            val mergedList = templates.map { template ->
                                val userAchievement = userAchievementsMap[template.id]
                                val isUnlocked = user.unlockedAchievements.contains(template.id) || userAchievement?.isUnlocked == true
                                val progressVal = getProgressForAchievement(template.id, user, isUnlocked, template.requirement)

                                UserAchievement(
                                    userId = userId,
                                    achievementId = template.id,
                                    achievement = template,
                                    isUnlocked = isUnlocked,
                                    unlockedAt = userAchievement?.unlockedAt ?: 0,
                                    isDisplayed = userAchievement?.isDisplayed ?: false,
                                    progress = progressVal
                                )
                            }
                            callback(Result.success(mergedList))
                        }
                        .addOnFailureListener { e ->
                            callback(Result.failure(e))
                        }
                }
                .addOnFailureListener { e ->
                    callback(Result.failure(e))
                }
        }
    }

    fun unlockAchievement(userId: String, achievementId: String, callback: (Result<Boolean>) -> Unit) {
        val userAchievementRef = FirestoreHelper.userAchievementsCollection(userId).document(achievementId)
        val data = mapOf(
            "userId" to userId,
            "achievementId" to achievementId,
            "isUnlocked" to true,
            "unlockedAt" to System.currentTimeMillis()
        )
        userAchievementRef.set(data, SetOptions.merge())
            .addOnSuccessListener { callback(Result.success(true)) }
            .addOnFailureListener { e -> callback(Result.failure(e)) }
    }

    fun updateAchievementProgress(userId: String, achievementId: String, progress: Int, callback: (Result<Boolean>) -> Unit) {
        val userAchievementRef = FirestoreHelper.userAchievementsCollection(userId).document(achievementId)
        
        FirestoreHelper.achievementsCollection().document(achievementId).get()
            .addOnSuccessListener { doc ->
                val achievement = doc.toObject(Achievement::class.java)
                val isUnlocked = achievement != null && progress >= achievement.requirement
                
                val data = mutableMapOf<String, Any>(
                    "userId" to userId,
                    "achievementId" to achievementId,
                    "progress" to progress,
                    "isUnlocked" to isUnlocked
                )
                if (isUnlocked) {
                    data["unlockedAt"] = System.currentTimeMillis()
                }

                userAchievementRef.set(data, SetOptions.merge())
                    .addOnSuccessListener { callback(Result.success(true)) }
                    .addOnFailureListener { e -> callback(Result.failure(e)) }
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }

    fun updateSelectedAchievements(userId: String, selectedIds: List<String>, callback: (Result<Boolean>) -> Unit) {
        
        checkAndTriggerAchievements(userId, "ubah_achievement", customUpdate = { user ->
            user.copy(selectedAchievements = selectedIds)
        }) { result ->
            if (result.isSuccess) {
                
                val selectedTitle = if (selectedIds.isNotEmpty()) {
                    val firstId = selectedIds.first()
                    TEMPLATE_ACHIEVEMENTS.find { it.id == firstId }?.title ?: "Pelajar"
                } else {
                    "Belum Ada"
                }
                FirestoreHelper.usersCollection().document(userId)
                    .update("selectedAchievement", selectedTitle)
                    .addOnCompleteListener {
                        callback(Result.success(true))
                    }
            } else {
                callback(Result.failure(result.exceptionOrNull() ?: Exception("Gagal update")))
            }
        }
    }

    fun checkAndTriggerAchievements(
        userId: String,
        eventName: String? = null,
        customUpdate: (User) -> User = { it },
        callback: (Result<User>) -> Unit
    ) {
        val userDocRef = FirestoreHelper.usersCollection().document(userId)
        userDocRef.get()
            .addOnSuccessListener { doc ->
                val rawUser = doc.toObject(User::class.java) ?: User(uid = userId)
                
                var user = customUpdate(rawUser)

                val earnedOneTime = user.earnedOneTimeXpEvents.toMutableList()
                var addedXp = 0
                if (eventName != null && XpCalculator.ONE_TIME_XP.containsKey(eventName)) {
                    if (!earnedOneTime.contains(eventName)) {
                        val xpAmount = XpCalculator.ONE_TIME_XP[eventName] ?: 0
                        addedXp += xpAmount
                        earnedOneTime.add(eventName)
                        Log.d(TAG, "Awarded first-time XP for event: $eventName, +$addedXp XP")
                    }
                }
                user = user.copy(
                    xp = user.xp + addedXp,
                    earnedOneTimeXpEvents = earnedOneTime
                )

                val unlockedList = user.unlockedAchievements.toMutableList()
                
                fun checkUnlock(id: String) {
                    if (!unlockedList.contains(id)) {
                        unlockedList.add(id)
                        Log.d(TAG, "Achievement unlocked: $id")
                        
                        val achRef = FirestoreHelper.userAchievementsCollection(userId).document(id)
                        achRef.set(
                            mapOf(
                                "userId" to userId,
                                "achievementId" to id,
                                "isUnlocked" to true,
                                "unlockedAt" to System.currentTimeMillis(),
                                "progress" to 1
                            ),
                            SetOptions.merge()
                        )
                        
                        com.unram.asakv2.achievement.AchievementUnlockManager.enqueue(id)
                    }
                }

                if (eventName == "daftar") checkUnlock("newbie")
                if (eventName == "ubah_nama" || unlockedList.contains("ubah_nama")) checkUnlock("ubah_nama")
                if (eventName == "ubah_tagline" || unlockedList.contains("ubah_tagline")) checkUnlock("ubah_tagline")
                if (eventName == "ubah_foto" || unlockedList.contains("ubah_foto")) checkUnlock("ubah_foto")
                if (eventName == "ubah_achievement" || unlockedList.contains("ubah_achievement")) checkUnlock("ubah_achievement")
                if (eventName == "penggunaan_ar" || unlockedList.contains("penggunaan_ar")) checkUnlock("penggunaan_ar")
                if (eventName == "belajar" || unlockedList.contains("belajar_pertama")) checkUnlock("belajar_pertama")

                if (user.completedStages.contains(1)) checkUnlock("acv21")
                if (user.completedStages.contains(2)) checkUnlock("acv22")
                if (user.completedStages.contains(3)) checkUnlock("acv23")
                if (user.completedStages.contains(4)) checkUnlock("acv24")
                if (user.completedStages.contains(5)) checkUnlock("acv25")
                if (user.completedStages.contains(6)) checkUnlock("selesai_stage_6")

                if (user.level >= 2) checkUnlock("naik_level_2")
                if (user.level >= 5) checkUnlock("mencapai_level_5")
                if (user.level >= 10) checkUnlock("mencapai_level_10")
                if (user.level >= 15) checkUnlock("mencapai_level_15")
                if (user.level >= 20) checkUnlock("level_max")

                if (user.streak >= 5) checkUnlock("streak_5")

                if (user.unlockedLetters.size >= 18) checkUnlock("buka_semua_huruf")
                if (user.unlockedWisata.size >= 8) checkUnlock("buka_semua_wisata")
                if (user.unlockedBudaya.size >= 12) checkUnlock("buka_semua_budaya")

                if (user.writingAccuracyHistory.any { it >= 90.0 }) checkUnlock("akurasi_menulis_90")
                if (user.speakingAccuracyHistory.any { it >= 90.0 }) checkUnlock("akurasi_mengucapkan_90")

                val otherUnlockedCount = unlockedList.filter { it != "unlock_24_achievement" }.size
                if (otherUnlockedCount >= 24) checkUnlock("unlock_24_achievement")

                user = user.copy(unlockedAchievements = unlockedList)

                val earnedMilestones = user.earnedMilestoneRewards.toMutableList()
                var milestoneAddedXp = 0
                val totalUnlocked = unlockedList.size
                
                for ((milestoneCount, xpReward) in XpCalculator.MILESTONE_XP) {
                    if (totalUnlocked >= milestoneCount && !earnedMilestones.contains(milestoneCount)) {
                        milestoneAddedXp += xpReward
                        earnedMilestones.add(milestoneCount)
                        Log.d(TAG, "Milestone reached: $milestoneCount achievements unlocked! +$xpReward XP")
                    }
                }
                
                user = user.copy(
                    xp = user.xp + milestoneAddedXp,
                    earnedMilestoneRewards = earnedMilestones
                )

                var evaluatedLevel = user.level
                while (evaluatedLevel < 20 && user.xp >= XpCalculator.xpRequiredForLevel(evaluatedLevel + 1)) {
                    evaluatedLevel++
                    Log.d(TAG, "Leveled up! New level: $evaluatedLevel")
                }
                
                if (evaluatedLevel != user.level) {
                    user = user.copy(level = evaluatedLevel)
                    if (evaluatedLevel >= 2) checkUnlock("naik_level_2")
                    if (evaluatedLevel >= 5) checkUnlock("mencapai_level_5")
                    if (evaluatedLevel >= 10) checkUnlock("mencapai_level_10")
                    if (evaluatedLevel >= 15) checkUnlock("mencapai_level_15")
                    if (evaluatedLevel >= 20) checkUnlock("level_max")
                    
                    val secondOtherUnlockedCount = unlockedList.filter { it != "unlock_24_achievement" }.size
                    if (secondOtherUnlockedCount >= 24) checkUnlock("unlock_24_achievement")
                    
                    user = user.copy(unlockedAchievements = unlockedList)
                }

                userDocRef.set(user, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "User document successfully updated in Firestore.")
                        callback(Result.success(user))
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed updating user in Firestore", e)
                        callback(Result.failure(e))
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed loading user profile", e)
                callback(Result.failure(e))
            }
    }
}
