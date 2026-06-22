package com.unram.asakv2.model

/**
 * Data class achievement — Definisi achievement yang tersedia.
 */
data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val category: String = "",      // Kategori: quiz, streak, level, ar, dll.
    val requirement: Int = 0,       // Syarat untuk unlock (jumlah yang harus dicapai)
    val requirementType: String = "" // Tipe syarat: total_quiz, streak_days, level, dll.
)
