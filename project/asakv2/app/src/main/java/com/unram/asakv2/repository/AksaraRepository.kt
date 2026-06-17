package com.unram.asakv2.repository

import com.unram.asakv2.model.AksaraHuruf

/**
 * AksaraRepository — Load data 18 huruf dari Firestore.
 * Mengelola operasi baca data aksara dari koleksi Firestore.
 * [RENDI]
 */
class AksaraRepository {

    fun getAllAksara(callback: (Result<List<AksaraHuruf>>) -> Unit) {
        // TODO: Load semua data 18 huruf aksara dari Firestore
    }

    fun getAksaraById(aksaraId: String, callback: (Result<AksaraHuruf?>) -> Unit) {
        // TODO: Load data aksara berdasarkan ID
    }
}
