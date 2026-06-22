package com.unram.asakv2.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object FirestoreHelper {

    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun usersCollection() = db.collection("users")
    fun aksaraCollection() = db.collection("aksara_huruf")
    fun achievementsCollection() = db.collection("achievements")
    fun arBudayaCollection() = db.collection("ar_budaya")
    fun levelsCollection() = db.collection("levels")

    fun userProgressCollection(userId: String) =
        usersCollection().document(userId).collection("huruf_progress")

    fun userAchievementsCollection(userId: String) =
        usersCollection().document(userId).collection("achievements")

    fun userQuizSessionsCollection(userId: String) =
        usersCollection().document(userId).collection("quiz_sessions")
}
