package org.uzi.kmpllama

import androidx.compose.runtime.Composable

@Composable
actual fun pickImage(onImagePicked: (String?) -> Unit) {
    // JVM implementation would go here - no-op for now
}

actual class ImageClassifier {
    actual suspend fun initialize(): Boolean {
        // JVM ExecuTorch implementation would go here
        return false
    }
    
    actual suspend fun classifyImage(imagePath: String): List<ClassificationResult> {
        // JVM implementation would go here
        return emptyList()
    }
}
