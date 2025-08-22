package org.uzi.kmpllama

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonImageClassificationScreen() {
    var isInitializing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isModelInitialized by remember { mutableStateOf(false) }
    
    val classifier = remember { ImageClassifier() }
    
    // Initialize model on first composition
    LaunchedEffect(Unit) {
        isInitializing = true
        try {
            isModelInitialized = classifier.initialize()
            if (!isModelInitialized) {
                errorMessage = "ExecuTorch not available on this platform"
            }
        } catch (e: Exception) {
            errorMessage = "Error initializing model: ${e.message}"
        } finally {
            isInitializing = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "MobileNetV2 Image Classification",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Powered by ExecuTorch",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (!isModelInitialized && !isInitializing) {
            // Platform not supported message
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🚧 Platform Support",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Blue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ExecuTorch image classification is currently implemented for Android only. " +
                              "iOS and Desktop support can be added using platform-specific ExecuTorch libraries.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "✨ Features on Android:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Blue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Load ExecuTorch MobileNetV2 model (mv2_xnnpack.pte)\n" +
                              "• Classify images into 1000 ImageNet categories\n" +
                              "• Show confidence scores for top predictions\n" +
                              "• Run entirely on-device with no server required",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            }
        } else {
            // Model Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isInitializing -> Color(0xFFFFA500).copy(alpha = 0.1f)
                        isModelInitialized -> Color.Green.copy(alpha = 0.1f)
                        else -> Color.Red.copy(alpha = 0.1f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isInitializing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFFFFA500)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Initializing ExecuTorch model...", fontSize = 14.sp)
                    } else if (isModelInitialized) {
                        Text("✅ Model Ready", fontSize = 14.sp, color = Color.Green)
                    } else {
                        Text("❌ Model Not Loaded", fontSize = 14.sp, color = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feature not available message
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "Image selection and classification features are available on Android only.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Error Message
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "⚠️ $error",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
