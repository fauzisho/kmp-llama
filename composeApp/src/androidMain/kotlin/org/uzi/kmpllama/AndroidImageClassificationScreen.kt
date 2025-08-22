package org.uzi.kmpllama

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import android.graphics.BitmapFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidImageClassificationScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isInitializing by remember { mutableStateOf(false) }
    var classificationResults by remember { mutableStateOf<List<ClassificationResult>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isModelInitialized by remember { mutableStateOf(false) }
    
    val classifier = remember { ImageClassifier() }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        classificationResults = emptyList()
        errorMessage = null
        
        // Load bitmap for preview
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            } catch (e: Exception) {
                errorMessage = "Failed to load image: ${e.message}"
            }
        }
    }
    
    // Initialize model on first composition
    LaunchedEffect(Unit) {
        isInitializing = true
        try {
            isModelInitialized = classifier.initializeWithContext(context)
            if (!isModelInitialized) {
                errorMessage = "Failed to initialize ExecuTorch model. Make sure mv2_xnnpack.pte is in assets folder."
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

        // Model Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isInitializing -> Color(0xFFFFA500).copy(alpha = 0.1f) // Orange
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
                        color = Color(0xFFFFA500) // Orange
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

        // Image Selection Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageBitmap != null) {
                    // Display the actual image
                    Image(
                        bitmap = selectedImageBitmap!!.asImageBitmap(),
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Overlay with status
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                Color.Green.copy(alpha = 0.8f),
                                RoundedCornerShape(bottomEnd = 8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "✓ Image Selected",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📷 No Image Selected",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap 'Select Image' to choose a photo",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier.weight(1f),
                enabled = !isInitializing
            ) {
                Text("Select Image")
            }

            Button(
                onClick = {
                    selectedImageUri?.let { uri ->
                        isLoading = true
                        errorMessage = null
                        
                        coroutineScope.launch {
                            try {
                                val results = classifier.classifyImageUri(uri)
                                classificationResults = results
                                if (results.isEmpty()) {
                                    errorMessage = "No classification results returned"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Classification failed: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = selectedImageUri != null && !isLoading && isModelInitialized,
                modifier = Modifier.weight(1f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White
                    )
                } else {
                    Text("Classify")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Error Message
        errorMessage?.let { error ->
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
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Results Section
        if (classificationResults.isNotEmpty()) {
            Text(
                text = "Classification Results",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(classificationResults) { result ->
                    ClassificationResultItem(result = result)
                }
            }
        } else if (isModelInitialized && selectedImageUri != null && !isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "💡 Tap 'Classify' to run the ExecuTorch model on your selected image",
                    color = Color.Blue,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun ClassificationResultItem(result: ClassificationResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.className.replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Confidence: ${(result.confidence * 100).formatToDecimalPlaces(2)}%",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Confidence bar
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(result.confidence)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when {
                                result.confidence > 0.7f -> Color.Green
                                result.confidence > 0.4f -> Color(0xFFFFA500) // Orange
                                else -> Color.Red
                            }
                        )
                )
            }
        }
    }
}

// Extension function to format float to specified decimal places
fun Float.formatToDecimalPlaces(digits: Int): String = String.format("%.${digits}f", this)
