package org.uzi.kmpllama

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull

class ComposeAppCommonTest {

    @Test
    fun testGreeting() {
        val greeting = Greeting().greet()
        assertContains(greeting, "Hello")
    }
    
    @Test
    fun testApiServiceInstantiation() {
        val apiService = ApiService()
        assertNotNull(apiService)
        // Clean up
        apiService.close()
    }
    
    @Test
    fun testVisionRequestSerialization() {
        val request = VisionRequest(
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        Content(type = "text", text = "Test question"),
                        Content(
                            type = "image_url",
                            imageUrl = ImageUrl("data:image/jpeg;base64,test")
                        )
                    )
                )
            )
        )
        
        // Verify the request structure
        assertContains(request.model, "smolvlm")
        assertContains(request.messages.first().role, "user")
        assertContains(request.messages.first().content.first().text ?: "", "Test")
    }
}
