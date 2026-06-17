package com.unram.asakv2.ar

import android.app.Activity

/**
 * ZXingScanner — Wrapper ZXing scan barcode/QR.
 * Mengelola proses scan barcode/QR code menggunakan ZXing library.
 * [DESTI]
 */
class ZXingScanner {

    fun startScan(activity: Activity, callback: (Result<String>) -> Unit) {
        // TODO: Launch ZXing scanner intent
        // TODO: Handle scan result
    }

    fun handleScanResult(resultCode: Int, data: android.content.Intent?): String? {
        // TODO: Parse scan result dari ZXing
        return null
    }
}
