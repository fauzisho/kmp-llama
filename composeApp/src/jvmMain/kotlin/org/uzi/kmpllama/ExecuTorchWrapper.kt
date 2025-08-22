package org.uzi.kmpllama

import androidx.compose.runtime.Composable

@Composable
actual fun ExecuTorchClassificationWrapper(
    onNavigateToCamera: () -> Unit
) {
    // JVM implementation uses the common screen 
    CommonImageClassificationScreen()
}
