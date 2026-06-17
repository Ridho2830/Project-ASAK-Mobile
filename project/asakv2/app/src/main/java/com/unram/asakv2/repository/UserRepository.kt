package com.unram.asakv2.repository

import com.google.firebase.firestore.FieldValue
import com.unram.asakv2.firebase.FirestoreHelper
import com.unram.asakv2.model.User

/**
 * UserRepository — CRUD user di Firestore.
 * Mengelola operasi baca/tulis data pengguna di Firebase Firestore.
 * [RIDHO]
 */
class UserRepository {

    fun createUserIfNotExists(user: User, callback: (Result<Boolean>) -> Unit) {
        val userRef = FirestoreHelper.usersCollection().document(user.uid)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                userRef.set(user)
                    .addOnSuccessListener { callback(Result.success(true)) }
                    .addOnFailureListener { e -> callback(Result.failure(e)) }
            } else {
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
        FirestoreHelper.usersCollection().document(userId)
            .update("name", newName)
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }

    fun updateUserXp(userId: String, xpToAdd: Int, callback: (Result<Boolean>) -> Unit) {
        FirestoreHelper.usersCollection().document(userId)
            .update("totalXp", FieldValue.increment(xpToAdd.toLong()))
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }

    fun updateStreak(userId: String, newStreak: Int, callback: (Result<Boolean>) -> Unit) {
        FirestoreHelper.usersCollection().document(userId)
            .update("streak", newStreak)
            .addOnSuccessListener {
                callback(Result.success(true))
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }
}
