package org.uzi.kmpllama

import androidx.compose.runtime.Composable

// Expect declarations for platform-specific image classification
@Composable
expect fun pickImage(onImagePicked: (String?) -> Unit)

expect class ImageClassifier() {
    suspend fun initialize(): Boolean
    suspend fun classifyImage(imagePath: String): List<ClassificationResult>
}
