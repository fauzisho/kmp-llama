package org.uzi.kmpllama

import android.net.Uri
import androidx.compose.runtime.Composable

// Android implementation of pickImage - no-op since we handle it directly in Android screen
@Composable
actual fun pickImage(onImagePicked: (String?) -> Unit) {
    // This is handled directly in AndroidImageClassificationScreen
    // No action needed here
}

// Android implementation of ImageClassifier
actual class ImageClassifier {
    private lateinit var context: android.content.Context
    private var execuTorchClassifier: ExecuTorchImageClassifier? = null
    
    actual suspend fun initialize(): Boolean {
        return try {
            // Return true for Android, false for other platforms
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    actual suspend fun classifyImage(imagePath: String): List<ClassificationResult> {
        return try {
            execuTorchClassifier?.let { classifier ->
                val uri = Uri.parse(imagePath)
                classifier.classifyImage(uri)
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun setContext(context: android.content.Context) {
        this.context = context
        this.execuTorchClassifier = ExecuTorchImageClassifier(context)
    }
    
    suspend fun initializeWithContext(context: android.content.Context): Boolean {
        this.context = context
        this.execuTorchClassifier = ExecuTorchImageClassifier(context)
        return execuTorchClassifier!!.initialize()
    }
    
    suspend fun classifyImageUri(imageUri: Uri): List<ClassificationResult> {
        return execuTorchClassifier?.classifyImage(imageUri) ?: emptyList()
    }
}
