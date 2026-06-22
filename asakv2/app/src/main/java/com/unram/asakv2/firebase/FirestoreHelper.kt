package com.unram.asakv2.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * FirestoreHelper — Koneksi + helper query Firestore.
 * Menyediakan instance Firestore dan method helper untuk query umum.
 * [RIDHO]
 */
object FirestoreHelper {

    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Collection references
    fun usersCollection() = db.collection("users")
    fun aksaraCollection() = db.collection("aksara_huruf")
    fun achievementsCollection() = db.collection("achievements")
    fun arBudayaCollection() = db.collection("ar_budaya")
    fun levelsCollection() = db.collection("levels")

    // User sub-collections
    fun userProgressCollection(userId: String) =
        usersCollection().document(userId).collection("huruf_progress")

    fun userAchievementsCollection(userId: String) =
        usersCollection().document(userId).collection("achievements")

    fun userQuizSessionsCollection(userId: String) =
        usersCollection().document(userId).collection("quiz_sessions")
}
