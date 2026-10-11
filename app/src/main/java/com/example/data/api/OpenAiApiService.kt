package com.example.data.api

import com.example.data.model.ApiSettings
import com.example.data.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class OpenAiApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendChatCompletion(
        settings: ApiSettings,
        history: List<ChatMessage>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (settings.baseUrl.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("URL API Custom tidak boleh kosong. Harap atur di menu Pengaturan."))
            }

            val targetUrl = buildEndpointUrl(settings.baseUrl)

            val rootJson = JSONObject()
            rootJson.put("model", settings.model.ifBlank { "gpt-4o-mini" })
            rootJson.put("temperature", settings.temperature.toDouble())

            val messagesArray = JSONArray()

            // System prompt
            if (settings.systemPrompt.isNotBlank()) {
                val sysMsg = JSONObject()
                sysMsg.put("role", "system")
                sysMsg.put("content", settings.systemPrompt)
                messagesArray.put(sysMsg)
            }

            // Chat history messages (exclude error messages and loading placeholders)
            history.filter { !it.isError && !it.isLoading }.takeLast(12).forEach { msg ->
                val mJson = JSONObject()
                mJson.put("role", msg.role)
                mJson.put("content", msg.content)
                messagesArray.put(mJson)
            }

            rootJson.put("messages", messagesArray)

            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)

            val requestBuilder = Request.Builder()
                .url(targetUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")

            if (settings.apiKey.isNotBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer ${settings.apiKey.trim()}")
            }

            val response = client.newCall(requestBuilder.build()).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                val errorMessage = parseErrorMessage(response.code, responseBody)
                return@withContext Result.failure(IOException(errorMessage))
            }

            val parsedResponse = JSONObject(responseBody)
            val choices = parsedResponse.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val firstChoice = choices.getJSONObject(0)
                val messageObj = firstChoice.optJSONObject("message")
                val textContent = messageObj?.optString("content").orEmpty()
                if (textContent.isNotBlank()) {
                    return@withContext Result.success(textContent.trim())
                }
            }

            Result.failure(IOException("Format respon dari server tidak sesuai standar OpenAI."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testConnection(settings: ApiSettings): Result<String> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val testMessage = ChatMessage(
                role = "user",
                content = "Ping! Balas singkat 1 kata: OK"
            )
            val result = sendChatCompletion(
                settings = settings.copy(systemPrompt = "Balas sesingkat mungkin."),
                history = listOf(testMessage)
            )
            val latency = System.currentTimeMillis() - startTime
            if (result.isSuccess) {
                Result.success("Terhubung! (${latency}ms) - Model merespon: \"${result.getOrNull()}\"")
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Gagal terhubung"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildEndpointUrl(rawUrl: String): String {
        val clean = rawUrl.trim().removeSuffix("/")
        return when {
            clean.endsWith("/chat/completions") -> clean
            clean.endsWith("/v1") -> "$clean/chat/completions"
            else -> "$clean/chat/completions"
        }
    }

    private fun parseErrorMessage(code: Int, body: String): String {
        return try {
            val json = JSONObject(body)
            val errorObj = json.optJSONObject("error")
            val msg = errorObj?.optString("message")
            if (!msg.isNullOrBlank()) {
                "HTTP $code: $msg"
            } else {
                "HTTP $code: $body"
            }
        } catch (_: Exception) {
            when (code) {
                401 -> "HTTP 401: API Key salah atau tidak memiliki izin."
                404 -> "HTTP 404: Endpoint / Model tidak ditemukan pada server."
                429 -> "HTTP 429: Batas kuota atau rate limit terlampaui."
                500 -> "HTTP 500: Server error pada penyedia AI."
                else -> "Gagal memproses permintaan (HTTP $code)"
            }
        }
    }
}
