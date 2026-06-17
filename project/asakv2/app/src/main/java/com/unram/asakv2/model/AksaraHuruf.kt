package com.unram.asakv2.model

/**
 * Data class 18 huruf aksara — Representasi huruf aksara Sasak.
 */
data class AksaraHuruf(
    val id: String = "",
    val name: String = "",          // Nama huruf (ha, na, ca, dll.)
    val imageUrl: String = "",      // URL gambar aksara (Firebase Storage)
    val localImagePath: String = "",// Path gambar lokal di assets
    val audioUrl: String = "",      // URL audio pengucapan (Firebase Storage)
    val localAudioPath: String = "",// Path audio lokal di assets
    val levelRequired: Int = 1,     // Level minimum untuk unlock huruf ini
    val orderIndex: Int = 0         // Urutan huruf dalam alfabet aksara
)
