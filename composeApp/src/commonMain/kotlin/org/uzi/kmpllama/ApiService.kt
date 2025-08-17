package org.uzi.kmpllama

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.client.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.timeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

// Data classes matching your working Android app structure - using kotlinx.serialization
@Serializable
data class ChatCompletionRequest(
    @SerialName("max_tokens") val maxTokens: Int = 100,
    @SerialName("messages") val messages: List<Message>
)

@Serializable
data class Message(
    @SerialName("role") val role: String,
    @SerialName("content") val content: List<Content>
)

@Serializable
data class Content(
    @SerialName("type") val type: String,
    @SerialName("text") val text: String? = null,
    @SerialName("image_url") val imageUrl: ImageUrl? = null
)

@Serializable
data class ImageUrl(
    @SerialName("url") val url: String
)

@Serializable
data class ChatCompletionResponse(
    @SerialName("choices") val choices: List<Choice>
)

@Serializable
data class Choice(
    @SerialName("message") val message: ResponseMessage
)

@Serializable
data class ResponseMessage(
    @SerialName("content") val content: String
)

class ApiService {
    private val client = HttpClient() {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }
    }
    
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    
    suspend fun analyzeImage(
        serverUrl: String,
        question: String,
        imageBase64: String
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            // Create request exactly like your working Android app
            val request = ChatCompletionRequest(
                maxTokens = 100,
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(
                            Content(type = "text", text = question),
                            Content(
                                type = "image_url",
                                imageUrl = ImageUrl(url = "data:image/jpeg;base64,$imageBase64")
                            )
                        )
                    )
                )
            )
            
            val requestBody = json.encodeToString(request)
            
            val response = client.post("$serverUrl/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
                timeout {
                    requestTimeoutMillis = 30000
                }
            }
            
            val responseBody = response.bodyAsText()
            
            if (response.status.isSuccess() && responseBody.isNotEmpty()) {
                val chatResponse = json.decodeFromString<ChatCompletionResponse>(responseBody)
                val content = chatResponse.choices.firstOrNull()?.message?.content
                
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("No content in response"))
                }
            } else {
                Result.failure(Exception("Server error: ${response.status} - $responseBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("API error: ${e.message}"))
        }
    }
}
