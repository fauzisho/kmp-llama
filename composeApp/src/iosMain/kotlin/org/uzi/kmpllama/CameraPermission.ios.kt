package org.uzi.kmpllama

import androidx.compose.runtime.Composable

@Composable
actual fun CameraPermissionHandler(
    content: @Composable () -> Unit
) {
    // iOS doesn't need runtime permission handling in Compose
    // AVFoundation permissions are handled at the native level
    content()
}
