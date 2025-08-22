package org.uzi.kmpllama

import androidx.compose.runtime.Composable

@Composable
actual fun ExecuTorchClassificationWrapper(
    onNavigateToCamera: () -> Unit
) {
    // iOS implementation uses the common screen
    CommonImageClassificationScreen()
}
