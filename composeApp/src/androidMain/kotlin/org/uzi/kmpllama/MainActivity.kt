package org.uzi.kmpllama

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Camera permission granted - restart the UI
            setContent {
                App()
            }
            Toast.makeText(this, "Camera permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            // Camera permission denied
            Toast.makeText(this, "Camera permission is required for this app to work properly", Toast.LENGTH_LONG).show()
            // Still show the app but with placeholder camera
            setContent {
                App()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check and request camera permission
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Camera permission already granted
                setContent {
                    App()
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show rationale and request permission
                Toast.makeText(this, "Camera permission is needed to capture images for AI analysis", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                // Request camera permission directly
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
