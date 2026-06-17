package com.unram.asakv2.utils

/**
 * Constants — Konstanta global aplikasi ASAK v2.
 * Menyimpan API URL, Firestore collection names, dan konfigurasi lainnya.
 */
object Constants {

    // API Base URLs
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    const val HUGGING_FACE_BASE_URL = "https://api-inference.huggingface.co/models/"

    // API Keys (sebaiknya disimpan di local.properties atau BuildConfig)
    const val GEMINI_API_KEY = "" // TODO: Isi dengan API key Gemini
    const val HUGGING_FACE_TOKEN = "" // TODO: Isi dengan token Hugging Face

    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_AKSARA = "aksara_huruf"
    const val COLLECTION_ACHIEVEMENTS = "achievements"
    const val COLLECTION_AR_BUDAYA = "ar_budaya"
    const val COLLECTION_LEVELS = "levels"

    // Sub-collections
    const val SUB_COLLECTION_PROGRESS = "huruf_progress"
    const val SUB_COLLECTION_USER_ACHIEVEMENTS = "achievements"
    const val SUB_COLLECTION_QUIZ_SESSIONS = "quiz_sessions"

    // Quiz
    const val TOTAL_QUIZ_QUESTIONS = 10
    const val QUIZ_TYPES_COUNT = 5

    // XP
    const val XP_PER_CORRECT_ANSWER = 10
    const val STREAK_MULTIPLIER_BASE = 1.5f

    // Levels
    const val MAX_LEVEL = 6
    const val TOTAL_AKSARA = 18
}
