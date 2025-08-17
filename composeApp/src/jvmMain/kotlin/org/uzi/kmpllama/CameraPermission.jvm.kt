package org.uzi.kmpllama

import androidx.compose.runtime.Composable

@Composable
actual fun CameraPermissionHandler(
    content: @Composable () -> Unit
) {
    // JVM doesn't need runtime permission handling
    // Webcam access is handled at the system level
    content()
}
