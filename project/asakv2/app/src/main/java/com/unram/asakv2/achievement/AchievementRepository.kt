package com.unram.asakv2.achievement

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unram.asakv2.model.User
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class AchievementRepository(context: Context) {

    private val expMap = ConcurrentHashMap<String, Int>()
    private val unlockedMap = ConcurrentHashMap<String, Boolean>()
    private val claimedMap = ConcurrentHashMap<String, Boolean>()
    private val notifiedMap = ConcurrentHashMap<String, Boolean>()
    private val showcaseSlots = CopyOnWriteArrayList<String?>()
    private var cachedUser: User? = null

    init {
        repeat(4) { showcaseSlots.add(null) }
    }

    fun loadFromFirestore(userId: String, callback: (Result<Boolean>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc ->
                val user = userDoc.toObject(User::class.java)
                cachedUser = user
                if (user != null) {
                    showcaseSlots.clear()
                    val selected = user.selectedAchievements
                    for (i in 0 until 4) {
                        val item = selected.getOrNull(i)
                        showcaseSlots.add(if (item.isNullOrEmpty()) null else item)
                    }
                    claimedMap.clear()
                    user.claimedAchievements.forEach { claimedMap[it] = true }
                }

                db.collection("users").document(userId).collection("achievements").get()
                    .addOnSuccessListener { subSnap ->
                        expMap.clear()
                        unlockedMap.clear()
                        for (doc in subSnap.documents) {
                            val id = doc.getString("achievementId") ?: continue
                            val progress = doc.getLong("progress")?.toInt() ?: 0
                            val isUnlocked = doc.getBoolean("isUnlocked") ?: false
                            expMap[id] = progress
                            unlockedMap[id] = isUnlocked
                        }
                        callback(Result.success(true))
                    }
                    .addOnFailureListener { e ->
                        callback(Result.failure(e))
                    }
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }

    fun getExp(id: String): Int {
        if (isUnlocked(id)) {
            val def = AchievementData.getById(id)
            if (def != null) return def.expRequired
        }
        val user = cachedUser ?: return expMap[id] ?: 0
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
            "akurasi_menulis_90" -> user.writingAccuracyHistory.maxOrNull()?.toInt() ?: (expMap[id] ?: 0)
            "akurasi_mengucapkan_90" -> user.speakingAccuracyHistory.maxOrNull()?.toInt() ?: (expMap[id] ?: 0)
            "acv21" -> if (user.completedStages.contains(1)) 1 else 0
            "acv22" -> if (user.completedStages.contains(2)) 1 else 0
            "acv23" -> if (user.completedStages.contains(3)) 1 else 0
            "acv24" -> if (user.completedStages.contains(4)) 1 else 0
            "acv25" -> if (user.completedStages.contains(5)) 1 else 0
            else -> expMap[id] ?: 0
        }
    }

    fun addExp(id: String, amount: Int): Boolean {
        val current = getExp(id)
        val def = AchievementData.getById(id) ?: return false
        if (current >= def.expRequired) return false
        val newVal = (current + amount).coerceAtMost(def.expRequired)
        expMap[id] = newVal
        if (newVal >= def.expRequired) {
            unlockedMap[id] = true
            return true
        }
        return false
    }

    fun isUnlocked(id: String): Boolean {
        val user = cachedUser
        if (user != null && user.unlockedAchievements.contains(id)) return true
        return unlockedMap[id] ?: false
    }

    fun isClaimed(id: String): Boolean = claimedMap[id] ?: false

    fun setClaimed(id: String, userId: String, callback: (Result<Boolean>) -> Unit) {
        claimedMap[id] = true
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                val newClaimed = user.claimedAchievements.toMutableList()
                if (!newClaimed.contains(id)) {
                    newClaimed.add(id)
                }

                val def = AchievementData.getById(id)
                val unlockedLet = user.unlockedLetters.toMutableList()
                val unlockedWis = user.unlockedWisata.toMutableList()
                val unlockedBud = user.unlockedBudaya.toMutableList()

                if (def != null) {
                    for (rewardId in def.rewardArIds) {
                        val isWisata = rewardId == "air_terjun" || rewardId == "pantai_kuta" ||
                                       rewardId == "sirkuit" || rewardId == "desaSade" ||
                                       rewardId == "desa_sade" || rewardId == "gn_rinjani" ||
                                       rewardId == "museum" || rewardId == "masjid_bayan" ||
                                       rewardId == "makam"

                        val isLetter = com.unram.asakv2.utils.Constants.HURUF_LIST.contains(rewardId) ||
                                       rewardId.startsWith("ar_aksara_")

                        if (isLetter) {
                            val code = if (rewardId.startsWith("ar_aksara_")) rewardId.substringAfter("ar_aksara_") else rewardId
                            if (!unlockedLet.contains(code)) {
                                unlockedLet.add(code)
                            }
                        } else if (isWisata) {
                            if (!unlockedWis.contains(rewardId)) {
                                unlockedWis.add(rewardId)
                            }
                        } else {
                            if (!unlockedBud.contains(rewardId)) {
                                unlockedBud.add(rewardId)
                            }
                        }
                    }
                }

                transaction.update(userRef, mapOf(
                    "claimedAchievements" to newClaimed,
                    "unlockedLetters" to unlockedLet,
                    "unlockedWisata" to unlockedWis,
                    "unlockedBudaya" to unlockedBud
                ))
            }
        }.addOnSuccessListener {
            callback(Result.success(true))
        }.addOnFailureListener { e ->
            callback(Result.failure(e))
        }
    }

    fun setClaimed(id: String) {
        claimedMap[id] = true
    }

    fun isNotified(id: String): Boolean = notifiedMap[id] ?: false

    fun setNotified(id: String) {
        notifiedMap[id] = true
    }

    fun getShowcaseSlots(): List<String?> = showcaseSlots.toList()

    fun setShowcaseSlot(index: Int, id: String?, userId: String, callback: (Result<Boolean>) -> Unit) {
        if (index in 0..3) {
            if (id != null) {
                
                for (i in 0 until 4) {
                    if (i != index && showcaseSlots[i] == id) {
                        showcaseSlots[i] = null
                    }
                }
            }
            showcaseSlots[index] = id
        }
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)
        val selectedIds = showcaseSlots.map { it ?: "" }

        userRef.update("selectedAchievements", selectedIds)
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }

    fun getUnlockedAndUnclaimed(): List<AchievementDef> =
        AchievementData.all.filter { isUnlocked(it.id) && !isClaimed(it.id) }

    fun hasUnclaimed(): Boolean = AchievementData.all.any { isUnlocked(it.id) && !isClaimed(it.id) }
}
