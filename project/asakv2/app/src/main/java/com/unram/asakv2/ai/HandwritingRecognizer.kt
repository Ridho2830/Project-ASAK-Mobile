package com.unram.asakv2.ai

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.unram.asakv2.model.CnnRequest
import com.unram.asakv2.network.RetrofitClient
import com.unram.asakv2.utils.Constants
import java.io.ByteArrayOutputStream

class HandwritingRecognizer {

    companion object {
        private const val TAG = "HandwritingRecognizer"
    }

    private fun cropBitmapToContent(src: Bitmap): Bitmap? {
        var minX = src.width; var minY = src.height
        var maxX = 0; var maxY = 0
        var found = false

        for (y in 0 until src.height) {
            for (x in 0 until src.width) {
                val pixel = src.getPixel(x, y)
                
                if (pixel != android.graphics.Color.WHITE) {
                    found = true
                    if (x < minX) minX = x
                    if (y < minY) minY = y
                    if (x > maxX) maxX = x
                    if (y > maxY) maxY = y
                }
            }
        }

        if (!found) return null

        val w = maxX - minX
        val h = maxY - minY
        if (w <= 0 || h <= 0) return null

        val cropped = Bitmap.createBitmap(src, minX, minY, w, h)

        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(result)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawBitmap(cropped, 0f, 0f, null)
        return result
    }
    suspend fun recognize(bitmap: Bitmap, huruf: String): Float {
        return try {
            val croppedBitmap = cropBitmapToContent(bitmap) ?: return 0f  
            val base64 = bitmapToBase64(croppedBitmap)  

            val hurufForApi = normalizeHurufForApi(huruf)

            val request = CnnRequest(
                image = "data:image/png;base64,$base64",
                huruf = hurufForApi,
                sizePrefix = 2
            )

            Log.d(TAG, "Mengirim request CNN: huruf=$hurufForApi, base64Length=${base64.length}")

            val response = RetrofitClient.cnnApi.checkHandwriting(request)

            Log.d(TAG, "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Response body: score=${body?.score}, label=${body?.label}")
                body?.score ?: 0f
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Request gagal: ${response.code()} - $errorBody")
                0f
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception saat recognize", e)
            e.printStackTrace()
            0f
        }
    }

    private fun normalizeHurufForApi(huruf: String): String {
        return huruf.lowercase().replaceFirstChar { it.uppercase() }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun isAccurate(score: Float): Boolean {
        return score >= Constants.CNN_ACCURACY_THRESHOLD
    }
}