package com.unram.asakv2.firebase

import com.google.firebase.storage.FirebaseStorage

/**
 * StorageHelper — Download URL model 3D dari Firebase Storage.
 * Menyediakan method untuk mengambil download URL file dari Firebase Storage.
 * [DESTI]
 */
object StorageHelper {

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    fun getDownloadUrl(path: String, callback: (Result<String>) -> Unit) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            callback(Result.success(path))
            return
        }
        storage.reference.child(path).downloadUrl
            .addOnSuccessListener { uri ->
                callback(Result.success(uri.toString()))
            }
            .addOnFailureListener { exception ->
                callback(Result.failure(exception))
            }
    }
}
