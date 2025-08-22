package org.uzi.kmpllama

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class ExecuTorchImageClassifier(private val context: Context) {
    
    private var modelPath: String? = null
    
    // ImageNet class labels (first 100 for demo - you'd want all 1000)
    private val imageNetClasses = listOf(
        "tench", "goldfish", "great white shark", "tiger shark", "hammerhead",
        "electric ray", "stingray", "cock", "hen", "ostrich",
        "brambling", "goldfinch", "house finch", "junco", "indigo bunting",
        "robin", "bulbul", "jay", "magpie", "chickadee",
        "water ouzel", "kite", "bald eagle", "vulture", "great grey owl",
        "European fire salamander", "common newt", "eft", "spotted salamander", "axolotl",
        "bullfrog", "tree frog", "tailed frog", "loggerhead", "leatherback turtle",
        "mud turtle", "terrapin", "box turtle", "banded gecko", "common iguana",
        "American chameleon", "whiptail", "agama", "frilled lizard", "alligator lizard",
        "Gila monster", "green lizard", "African chameleon", "Komodo dragon", "African crocodile",
        "American alligator", "triceratops", "thunder snake", "ringneck snake", "hognose snake",
        "green snake", "king snake", "garter snake", "water snake", "vine snake",
        "night snake", "boa constrictor", "rock python", "Indian cobra", "green mamba",
        "sea snake", "horned viper", "diamondback", "sidewinder", "trilobite",
        "harvestman", "scorpion", "black and gold garden spider", "barn spider", "garden spider",
        "black widow", "tarantula", "wolf spider", "tick", "centipede",
        "black grouse", "ptarmigan", "ruffed grouse", "prairie chicken", "peacock",
        "quail", "partridge", "African grey", "macaw", "sulphur-crested cockatoo",
        "lorikeet", "coucal", "bee eater", "hornbill", "hummingbird",
        "jacamar", "toucan", "drake", "red-breasted merganser", "goose",
        "black swan", "tusker", "echidna", "platypus", "wallaby",
        "koala", "wombat", "jellyfish", "sea anemone", "brain coral",
        "flatworm", "nematode", "conch", "snail", "slug", "sea slug", "chiton"
    )
    
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Copy model from assets to internal storage
            val modelFile = File(context.filesDir, "mv2_xnnpack.pte")
            if (!modelFile.exists()) {
                context.assets.open("mv2_xnnpack.pte").use { input ->
                    modelFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            modelPath = modelFile.absolutePath
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun classifyImage(imageUri: Uri): List<ClassificationResult> = withContext(Dispatchers.IO) {
        try {
            if (modelPath == null) {
                throw IllegalStateException("Model not initialized")
            }
            
            // Load and preprocess image
            val bitmap = loadAndPreprocessImage(imageUri)
            val inputTensor = bitmapToFloatArray(bitmap)
            
            // TODO: Replace with actual ExecuTorch inference
            // For now, return mock results based on image analysis
            val mockResults = generateMockResults()
            
            mockResults
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    private fun loadAndPreprocessImage(imageUri: Uri): Bitmap {
        val inputStream: InputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Cannot open image")
        
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        
        // Resize to 224x224 as required by MobileNetV2
        return Bitmap.createScaledBitmap(originalBitmap, 224, 224, true)
    }
    
    private fun bitmapToFloatArray(bitmap: Bitmap): FloatArray {
        val intValues = IntArray(224 * 224)
        bitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)
        
        val floatValues = FloatArray(224 * 224 * 3)
        
        // Normalize pixel values to [0, 1] and convert to RGB
        // ImageNet normalization: mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]
        val meanR = 0.485f
        val meanG = 0.456f
        val meanB = 0.406f
        val stdR = 0.229f
        val stdG = 0.224f
        val stdB = 0.225f
        
        for (i in intValues.indices) {
            val pixelValue = intValues[i]
            
            val r = ((pixelValue shr 16) and 0xFF) / 255.0f
            val g = ((pixelValue shr 8) and 0xFF) / 255.0f
            val b = (pixelValue and 0xFF) / 255.0f
            
            // Apply ImageNet normalization
            floatValues[i * 3] = (r - meanR) / stdR     // R
            floatValues[i * 3 + 1] = (g - meanG) / stdG // G
            floatValues[i * 3 + 2] = (b - meanB) / stdB // B
        }
        
        return floatValues
    }
    
    private fun generateMockResults(): List<ClassificationResult> {
        // Generate some realistic mock results
        val results = mutableListOf<ClassificationResult>()
        val indices = (0 until minOf(imageNetClasses.size, 5)).shuffled()
        
        var remainingProbability = 1.0f
        indices.forEachIndexed { index, classIndex ->
            val confidence = if (index == 0) {
                // First result gets highest confidence
                (0.3f + Math.random().toFloat() * 0.6f).coerceAtMost(remainingProbability)
            } else {
                // Subsequent results get decreasing confidence
                (Math.random().toFloat() * remainingProbability * 0.3f)
            }
            
            results.add(
                ClassificationResult(
                    className = imageNetClasses[classIndex],
                    confidence = confidence
                )
            )
            remainingProbability -= confidence
        }
        
        return results.sortedByDescending { it.confidence }
    }
    
    // TODO: Implement actual ExecuTorch inference
    private suspend fun runInference(inputTensor: FloatArray): FloatArray {
        // This is where you would load and run the ExecuTorch model
        // For now, return mock output
        return FloatArray(1000) { Math.random().toFloat() }
    }
    
    private fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.maxOrNull() ?: 0f
        val expLogits = logits.map { kotlin.math.exp((it - maxLogit).toDouble()).toFloat() }
        val sumExp = expLogits.sum()
        return expLogits.map { it / sumExp }.toFloatArray()
    }
}
