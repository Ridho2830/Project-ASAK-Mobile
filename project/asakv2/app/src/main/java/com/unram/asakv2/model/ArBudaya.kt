package com.unram.asakv2.model

/**
 * Data class konten AR budaya — Representasi konten AR budaya yang bisa di-unlock.
 */
data class ArBudaya(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val barcode: String = "",       // Kode barcode/QR untuk scan
    val model3DUrl: String = "",    // URL model 3D di Firebase Storage
    val thumbnailUrl: String = "",  // URL thumbnail preview
    val category: String = "",      // Kategori budaya
    val isUnlocked: Boolean = false
)
