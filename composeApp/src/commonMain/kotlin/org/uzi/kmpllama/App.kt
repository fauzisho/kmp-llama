package org.uzi.kmpllama

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF6366F1),
            primaryContainer = Color(0xFF4F46E5),
            surface = Color(0xFF1F2937),
            background = Color(0xFF111827),
            onSurface = Color.White,
            onBackground = Color.White
        )
    ) {
        CameraPermissionHandler {
            SmolVLMCameraScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmolVLMCameraScreen() {
    var serverUrl by remember { mutableStateOf("http://192.168.0.244:8080") }
    var question by remember { mutableStateOf("What do you see?") }
    var selectedInterval by remember { mutableStateOf("500ms") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var response by remember { mutableStateOf("Ready to start analysis...") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var bottomSheetHeight by remember { mutableStateOf(300.dp) }
    
    val intervals = listOf("100ms", "500ms", "1 second", "2 seconds", "5 seconds")
    val apiService = remember { ApiService() }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    // Analysis job
    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            while (isAnalyzing) {
                try {
                    response = "📸 Capturing image..."
                    
                    // Capture image and analyze
                    captureAndAnalyze(apiService, serverUrl, question) { result ->
                        response = result
                    }
                    
                    // Wait for the specified interval
                    val delayMs = when (selectedInterval) {
                        "100ms" -> 100L
                        "500ms" -> 500L
                        "1 second" -> 1000L
                        "2 seconds" -> 2000L
                        "5 seconds" -> 5000L
                        else -> 500L
                    }
                    delay(delayMs)
                } catch (e: Exception) {
                    response = "❌ Error: ${e.message}"
                    isAnalyzing = false
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Full-screen Camera Preview
        CameraPreview()
        
        // Analysis overlay when running - REMOVED
        // We'll just show responses in the floating card instead
        
        // Top Header with minimal info
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.9f),
            color = Color.Black.copy(alpha = 0.7f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "SmolVLM Camera",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                // Removed redundant progress indicator from header
            }
        }
        
        // Response overlay (when there's a response) - Now shows immediately
        if (response != "Ready to start analysis...") {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = bottomSheetHeight)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.85f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Show small progress indicator only if analyzing and response contains capturing/sending
                        if (isAnalyzing && (response.contains("Capturing") || response.contains("Sending"))) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF10B981),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Response",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6366F1)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = response,
                                fontSize = 14.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
        
        // Bottom Sheet - Draggable Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomSheetHeight)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val newHeight = bottomSheetHeight - with(density) { dragAmount.y.toDp() }
                        bottomSheetHeight = newHeight.coerceIn(120.dp, 500.dp)
                    }
                }
        ) {
            // Drag Handle
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                color = Color(0xFF1F2937),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (bottomSheetHeight > 250.dp) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = "Drag to resize",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Bottom Sheet Content
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF1F2937)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Quick Start/Stop Button (Always visible)
                    Button(
                        onClick = { 
                            isAnalyzing = !isAnalyzing
                            if (!isAnalyzing) {
                                response = "Analysis stopped."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAnalyzing) Color(0xFFEF4444) else Color(0xFF10B981)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (isAnalyzing) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isAnalyzing) "Stop Analysis" else "Start Analysis",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Small progress indicator in button when analyzing
                        if (isAnalyzing) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Server Configuration
                    Text(
                        text = "Server Configuration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        OutlinedTextField(
                            value = serverUrl,
                            onValueChange = { serverUrl = it },
                            label = { Text("Server URL", color = Color.Gray) },
                            placeholder = { Text("http://192.168.0.244:8080", color = Color.Gray) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            enabled = !isAnalyzing,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF374151),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            supportingText = {
                                NetworkUtils.validateServerUrl(serverUrl)?.let { error ->
                                    Text(
                                        text = error,
                                        color = Color(0xFFEF4444),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        )
                        
                        Button(
                            onClick = {
                                scope.launch {
                                    response = "🔍 Testing connection..."
                                    
                                    // First test basic connectivity
                                    val basicTest = NetworkUtils.testServerConnection(serverUrl)
                                    basicTest.fold(
                                        onSuccess = { message -> 
                                            response = message
                                            
                                            // If basic test passes, test SmolVLM endpoint
                                            response = "🤖 Testing SmolVLM endpoint..."
                                            val smolTest = DebugUtils.testSmolVLMEndpoint(serverUrl)
                                            smolTest.fold(
                                                onSuccess = { msg -> response = msg },
                                                onFailure = { error -> response = error.message ?: "SmolVLM test failed" }
                                            )
                                        },
                                        onFailure = { error -> response = error.message ?: "Connection test failed" }
                                    )
                                }
                            },
                            enabled = !isAnalyzing && NetworkUtils.validateServerUrl(serverUrl) == null,
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            )
                        ) {
                            Text("Test", fontSize = 12.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Question
                    Text(
                        text = "Analysis Question",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = question,
                        onValueChange = { question = it },
                        label = { Text("Question", color = Color.Gray) },
                        placeholder = { Text("What do you see?", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        enabled = !isAnalyzing,
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedBorderColor = Color(0xFF374151),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    
                    // Interval Selection
                    Text(
                        text = "Analysis Interval",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { if (!isAnalyzing) isDropdownExpanded = !isDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedInterval,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            enabled = !isAnalyzing,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF374151),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            intervals.forEach { interval ->
                                DropdownMenuItem(
                                    text = { Text(interval, color = Color.White) },
                                    onClick = {
                                        selectedInterval = interval
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Network Tips (if there's space)
                    if (bottomSheetHeight > 400.dp) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF374151).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "💡 Network Tips:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• For Android Emulator: Use 10.0.2.2 instead of localhost\n" +
                                          "• For Physical Device: Use your computer's local IP (192.168.x.x)\n" +
                                          "• Ensure SmolVLM server is running and accessible",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                    
                    // Extra space for scrolling
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
expect fun CameraPreview()

@Composable
expect fun CameraPermissionHandler(
    content: @Composable () -> Unit
)

expect fun captureAndAnalyze(
    apiService: ApiService,
    serverUrl: String,
    question: String,
    onResult: (String) -> Unit
)
