package com.unram.asakv2.model

/**
 * Data class level 1–6 — Definisi level pembelajaran aksara.
 */
data class Level(
    val level: Int = 1,
    val title: String = "",
    val description: String = "",
    val xpRequired: Int = 0,       // Total XP yang dibutuhkan untuk mencapai level ini
    val hurufUnlocked: List<String> = emptyList() // ID huruf yang ter-unlock di level ini
)
