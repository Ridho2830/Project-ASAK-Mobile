package com.unram.asakv2.utils

object Constants {

    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    const val HUGGING_FACE_BASE_URL = "https://api-inference.huggingface.co/models/"

    const val CNN_API_URL = "https://rendi2105-asak-handwriting-recognition.hf.space/"
    const val HF_SPACE_URL = "https://rendi2105-asak-voice-recognition.hf.space/"

    const val GEMINI_API_KEY = "" 
    const val HUGGING_FACE_TOKEN = "" 

    const val COLLECTION_USERS = "users"
    const val COLLECTION_AKSARA = "aksara_huruf"
    const val COLLECTION_ACHIEVEMENTS = "achievements"
    const val COLLECTION_AR_BUDAYA = "ar_budaya"
    const val COLLECTION_LEVELS = "levels"

    const val SUB_COLLECTION_PROGRESS = "huruf_progress"
    const val SUB_COLLECTION_USER_ACHIEVEMENTS = "achievements"
    const val SUB_COLLECTION_QUIZ_SESSIONS = "quiz_sessions"

    const val TOTAL_QUIZ_QUESTIONS = 10
    const val QUIZ_TYPES_COUNT = 5

    const val XP_PER_CORRECT_ANSWER = 10
    const val STREAK_MULTIPLIER_BASE = 1.5f

    const val MAX_LEVEL = 20
    const val TOTAL_AKSARA = 18

    const val CNN_ACCURACY_THRESHOLD = 80.0f
    const val VOICE_ACCURACY_THRESHOLD = 80.0f
    const val STAGE_PASS_THRESHOLD = 0.75f
    const val MIN_SIMILARITY_COLLECTOR = 90.0f

    const val VOICE_RECORD_DURATION_MS = 3000L

    val HURUF_LIST = listOf(
        "ha", "na", "ca", "ra", "ka",
        "da", "ta", "sa", "wa", "la",
        "ma", "ga", "ba", "nga", "pa",
        "ja", "ya", "nya"
    )

    val HURUF_DISPLAY = mapOf(
        "ha" to "Ha", "na" to "Na", "ca" to "Ca",
        "ra" to "Ra", "ka" to "Ka", "da" to "Da",
        "ta" to "Ta", "sa" to "Sa", "wa" to "Wa",
        "la" to "La", "ma" to "Ma", "ga" to "Ga",
        "ba" to "Ba", "nga" to "Nga", "pa" to "Pa",
        "ja" to "Ja", "ya" to "Ya", "nya" to "Nya"
    )

    const val ASSETS_IMAGE_PATH = "aksara/gambar"
    const val ASSETS_AUDIO_PATH = "aksara/audio"
    const val ASSETS_MARKER_PATH = "ar/markers"
}
