package org.uzi.kmpllama

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual fun captureAndAnalyze(
    apiService: ApiService,
    serverUrl: String,
    question: String,
    onResult: (String) -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            captureCurrentFrame { bitmap ->
                if (bitmap != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val base64Image = bitmapToBase64(bitmap)
                            val result = apiService.analyzeImage(serverUrl, question, base64Image.removePrefix("data:image/jpeg;base64,"))
                            
                            withContext(Dispatchers.Main) {
                                result.fold(
                                    onSuccess = { response ->
                                        onResult("✅ $response")
                                    },
                                    onFailure = { error ->
                                        onResult("❌ Error: ${error.message}")
                                    }
                                )
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                onResult("❌ Error processing image: ${e.message}")
                            }
                        }
                    }
                } else {
                    onResult("❌ Failed to capture image")
                }
            }
        } catch (e: Exception) {
            onResult("❌ Error: ${e.message}")
        }
    }
}

// Exact same bitmapToBase64 function as your working Android app
private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()

    // Scale down the image to a reasonable size (similar to HTML canvas behavior)
    val maxSize = 640
    val ratio = Math.min(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height)
    val scaledWidth = (bitmap.width * ratio).toInt()
    val scaledHeight = (bitmap.height * ratio).toInt()

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)

    // Compress with quality similar to HTML (0.8 = 80%)
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    val byteArray = outputStream.toByteArray()

    // Clean up
    if (scaledBitmap != bitmap) {
        scaledBitmap.recycle()
    }

    return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
