package com.example.data.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val isLoading: Boolean = false
) {
    val isUser: Boolean
        get() = role == "user"
}
