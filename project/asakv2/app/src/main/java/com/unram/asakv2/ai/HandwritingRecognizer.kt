package com.unram.asakv2.ai

import android.graphics.Bitmap
import com.unram.asakv2.network.HuggingFacePrediction

/**
 * HandwritingRecognizer — Kirim gambar canvas ke Hugging Face (CNN).
 * Mengirim bitmap dari CanvasView ke model CNN di Hugging Face untuk pengenalan aksara.
 * [RENDI]
 */
class HandwritingRecognizer {

    fun recognize(bitmap: Bitmap, callback: (Result<List<HuggingFacePrediction>>) -> Unit) {
        // TODO: Konversi bitmap ke file/byte array
        // TODO: Kirim ke Hugging Face API via RetrofitClient
        // TODO: Parse dan return hasil prediksi
    }

    fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        // TODO: Resize, normalize, dan preprocess bitmap sebelum dikirim ke CNN
        return bitmap
    }
}
