package com.unram.asakv2.repository

import com.unram.asakv2.model.ArBudaya

/**
 * ArRepository — Load model 3D URL (Firebase Storage).
 * Mengelola operasi baca data AR budaya dan URL model 3D.
 * [DESTI]
 */
class ArRepository {

    fun getArBudayaByBarcode(barcode: String, callback: (Result<ArBudaya?>) -> Unit) {
        // TODO: Load data AR budaya berdasarkan barcode dari Firestore
    }

    fun getModel3DUrl(modelPath: String, callback: (Result<String>) -> Unit) {
        // TODO: Load download URL model 3D dari Firebase Storage
    }

    fun unlockArBudaya(userId: String, arBudayaId: String, callback: (Result<Boolean>) -> Unit) {
        // TODO: Simpan unlock AR budaya ke Firestore
    }
}
