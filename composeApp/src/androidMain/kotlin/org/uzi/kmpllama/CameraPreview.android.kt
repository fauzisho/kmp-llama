package org.uzi.kmpllama

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executor

// Global variables to hold the current camera state
var currentImageCapture: ImageCapture? = null
var currentCameraProvider: ProcessCameraProvider? = null
var cameraContext: android.content.Context? = null

@Composable
actual fun CameraPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    AndroidView(
        factory = { ctx ->
            cameraContext = ctx
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)
            
            startCamera(previewView, executor, lifecycleOwner)
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun startCamera(
    previewView: PreviewView,
    executor: Executor,
    lifecycleOwner: LifecycleOwner
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
    
    cameraProviderFuture.addListener({
        try {
            currentCameraProvider = cameraProviderFuture.get()
            
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            
            currentImageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setJpegQuality(80)
                .build()
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            currentCameraProvider?.unbindAll()
            currentCameraProvider?.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, currentImageCapture
            )
        } catch (exc: Exception) {
            // Camera initialization failed
        }
    }, executor)
}

// Function to capture current frame
fun captureCurrentFrame(onImageCaptured: (Bitmap?) -> Unit) {
    currentImageCapture?.let { capture ->
        cameraContext?.let { context ->
            val outputFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
            
            capture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        try {
                            val bitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
                            onImageCaptured(bitmap)
                        } catch (e: Exception) {
                            onImageCaptured(null)
                        } finally {
                            try {
                                outputFile.delete()
                            } catch (e: Exception) {
                                // Ignore cleanup errors
                            }
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        onImageCaptured(null)
                    }
                }
            )
        } ?: onImageCaptured(null)
    } ?: onImageCaptured(null)
}
