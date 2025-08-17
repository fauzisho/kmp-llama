package org.uzi.kmpllama

import io.ktor.client.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NetworkUtils {
    private val testClient = HttpClient()
    
    suspend fun testServerConnection(serverUrl: String): Result<String> = withContext(Dispatchers.Default) {
        try {
            val response = testClient.get(serverUrl) {
                timeout {
                    requestTimeoutMillis = 10000
                    connectTimeoutMillis = 5000
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK -> Result.success("✅ Server is reachable")
                HttpStatusCode.NotFound -> Result.success("⚠️ Server reachable but endpoint not found")
                else -> Result.success("⚠️ Server responded with: ${response.status}")
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("network security policy", ignoreCase = true) == true -> 
                    "❌ Blocked by Android security policy. HTTP traffic not allowed."
                e.message?.contains("connection", ignoreCase = true) == true -> 
                    "❌ Cannot connect to server. Check if server is running."
                e.message?.contains("timeout", ignoreCase = true) == true -> 
                    "❌ Connection timeout. Server may be slow or unreachable."
                e.message?.contains("host", ignoreCase = true) == true -> 
                    "❌ Cannot resolve hostname. Check server URL."
                else -> "❌ Network error: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }
    
    fun validateServerUrl(url: String): String? {
        return when {
            url.isBlank() -> "Server URL cannot be empty"
            !url.startsWith("http://") && !url.startsWith("https://") -> 
                "Server URL must start with http:// or https://"
            url.contains("localhost") && !url.contains("10.0.2.2") -> 
                "For Android emulator, use 10.0.2.2 instead of localhost"
            else -> null // Valid URL
        }
    }
}
