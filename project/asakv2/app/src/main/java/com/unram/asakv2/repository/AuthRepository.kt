package com.unram.asakv2.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.unram.asakv2.model.User

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    fun firebaseAuthWithGoogle(idToken: String, callback: (Result<String>) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val newUser = User(
                            uid = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "Pelajar",
                            email = firebaseUser.email ?: "",
                            photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                        )
                        userRepository.createUserIfNotExists(newUser) { createResult ->
                            if (createResult.isSuccess) {
                                callback(Result.success("Login successful: ${newUser.name}"))
                            } else {
                                callback(Result.failure(createResult.exceptionOrNull() ?: Exception("Failed to create user in Firestore")))
                            }
                        }
                    } else {
                        callback(Result.failure(Exception("Firebase user is null after successful login.")))
                    }
                } else {
                    callback(Result.failure(task.exception ?: Exception("Authentication failed.")))
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
