package com.unram.asakv2.backend.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import io.ktor.server.application.*
import java.io.InputStream

fun Application.configureFirebase() {
    val serviceAccount: InputStream? = this::class.java.classLoader.getResourceAsStream("serviceAccountKey.json")
    if (serviceAccount != null) {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
            log.info("Firebase Admin SDK initialized successfully")
        }
    } else {
        log.error("Failed to load serviceAccountKey.json from resources")
    }
}

val db by lazy { FirestoreClient.getFirestore() }
