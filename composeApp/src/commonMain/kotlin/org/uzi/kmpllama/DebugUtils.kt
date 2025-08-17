package org.uzi.kmpllama

import io.ktor.client.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DebugUtils {
    private val debugClient = HttpClient()
    
    suspend fun testSmolVLMEndpoint(serverUrl: String): Result<String> = withContext(Dispatchers.Default) {
        try {
            // Simple test request to the SmolVLM endpoint
            val testRequest = """
            {
                "model": "smolvlm",
                "messages": [
                    {
                        "role": "user",
                        "content": [
                            {"type": "text", "text": "Hello, can you respond?"}
                        ]
                    }
                ],
                "max_tokens": 50
            }
            """.trimIndent()
            
            println("Testing SmolVLM endpoint: $serverUrl/v1/chat/completions")
            
            val response = debugClient.post("$serverUrl/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                setBody(testRequest)
                timeout {
                    requestTimeoutMillis = 30000
                    connectTimeoutMillis = 10000
                }
            }
            
            val responseText = response.bodyAsText()
            println("Debug response status: ${response.status}")
            println("Debug response body: $responseText")
            
            if (response.status.isSuccess()) {
                Result.success("✅ SmolVLM endpoint responding: ${response.status}")
            } else {
                Result.failure(Exception("❌ SmolVLM error: ${response.status} - $responseText"))
            }
        } catch (e: Exception) {
            println("Debug test failed: ${e.message}")
            Result.failure(Exception("❌ Debug test failed: ${e.message}"))
        }
    }
    
    suspend fun simpleGetTest(serverUrl: String): Result<String> = withContext(Dispatchers.Default) {
        try {
            val response = debugClient.get(serverUrl) {
                timeout {
                    requestTimeoutMillis = 10000
                    connectTimeoutMillis = 5000
                }
            }
            
            Result.success("✅ Server reachable via GET: ${response.status}")
        } catch (e: Exception) {
            Result.failure(Exception("❌ GET test failed: ${e.message}"))
        }
    }
}
