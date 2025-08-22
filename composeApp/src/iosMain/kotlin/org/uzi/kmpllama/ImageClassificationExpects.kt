package org.uzi.kmpllama

import androidx.compose.runtime.Composable

@Composable
actual fun pickImage(onImagePicked: (String?) -> Unit) {
    // iOS implementation would go here - no-op for now
}

actual class ImageClassifier {
    actual suspend fun initialize(): Boolean {
        // iOS ExecuTorch implementation would go here
        return false
    }
    
    actual suspend fun classifyImage(imagePath: String): List<ClassificationResult> {
        // iOS implementation would go here
        return emptyList()
    }
}
