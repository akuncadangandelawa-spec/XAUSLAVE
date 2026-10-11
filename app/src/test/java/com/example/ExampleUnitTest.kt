package com.example

import com.example.data.model.ApiSettings
import com.example.data.model.ChatMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testDefaultApiSettings() {
        val settings = ApiSettings()
        assertEquals("https://api.openai.com/v1", settings.baseUrl)
        assertEquals("gpt-4o-mini", settings.model)
        assertEquals(0.7f, settings.temperature, 0.01f)
        assertTrue(settings.systemPrompt.contains("XAU/USD"))
    }

    @Test
    fun testChatMessageProperties() {
        val userMsg = ChatMessage(role = "user", content = "Analisa XAUUSD hari ini")
        assertTrue(userMsg.isUser)
        assertFalse(userMsg.isError)

        val assistantMsg = ChatMessage(role = "assistant", content = "Tren emas sideways...")
        assertFalse(assistantMsg.isUser)
        assertFalse(assistantMsg.isError)
    }
}
