package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.OpenAiApiService
import com.example.data.model.ApiSettings
import com.example.data.model.ChatMessage
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SplitLayoutMode(val chartWeight: Float, val chatWeight: Float, val label: String) {
    BALANCED(0.5f, 0.5f, "50:50"),
    CHART_FOCUS(0.72f, 0.28f, "Chart Focus"),
    CHAT_FOCUS(0.28f, 0.72f, "Chat Focus")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application.applicationContext)
    private val openAiApiService = OpenAiApiService()

    private val _settings = MutableStateFlow(ApiSettings())
    val settings: StateFlow<ApiSettings> = _settings.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    private val _testConnectionResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val testConnectionResult: StateFlow<Pair<Boolean, String>?> = _testConnectionResult.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _splitMode = MutableStateFlow(SplitLayoutMode.BALANCED)
    val splitMode: StateFlow<SplitLayoutMode> = _splitMode.asStateFlow()

    private var lastPrompt: String? = null

    init {
        // Collect persistent settings from Jetpack DataStore
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { loadedSettings ->
                _settings.value = loadedSettings
            }
        }
    }

    fun setSplitMode(mode: SplitLayoutMode) {
        _splitMode.value = mode
    }

    fun toggleChartExpand() {
        _splitMode.value = if (_splitMode.value == SplitLayoutMode.CHART_FOCUS) {
            SplitLayoutMode.BALANCED
        } else {
            SplitLayoutMode.CHART_FOCUS
        }
    }

    fun openSettingsDialog() {
        _testConnectionResult.value = null
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
        _testConnectionResult.value = null
    }

    fun saveSettings(newSettings: ApiSettings) {
        viewModelScope.launch {
            settingsRepository.saveSettings(newSettings)
            _settings.value = newSettings
            closeSettingsDialog()
        }
    }

    fun testConnection(settingsToTest: ApiSettings) {
        viewModelScope.launch {
            _isTestingConnection.value = true
            _testConnectionResult.value = null

            val result = openAiApiService.testConnection(settingsToTest)
            if (result.isSuccess) {
                _testConnectionResult.value = Pair(true, result.getOrNull() ?: "Berhasil terhubung!")
            } else {
                _testConnectionResult.value = Pair(
                    false,
                    result.exceptionOrNull()?.localizedMessage ?: "Gagal terhubung ke endpoint."
                )
            }
            _isTestingConnection.value = false
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
    }

    fun retryLast() {
        lastPrompt?.let { sendMessage(it) }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isLoading.value) return
        lastPrompt = userText

        val currentList = _messages.value.toMutableList()
        val userMessage = ChatMessage(role = "user", content = userText)
        currentList.add(userMessage)
        _messages.value = currentList

        // Check if API key is missing
        if (_settings.value.apiKey.isBlank() && !_settings.value.baseUrl.contains("11434")) {
            val warningMessage = ChatMessage(
                role = "assistant",
                content = "API Key belum dikonfigurasi. Silakan klik tombol 'Atur API' atau icon Pengaturan di sudut kanan atas untuk memasukkan API Key dan model Anda.",
                isError = true
            )
            _messages.value = _messages.value + warningMessage
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = openAiApiService.sendChatCompletion(
                settings = _settings.value,
                history = _messages.value
            )

            if (result.isSuccess) {
                val assistantResponse = ChatMessage(
                    role = "assistant",
                    content = result.getOrNull().orEmpty()
                )
                _messages.value = _messages.value + assistantResponse
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Terjadi kesalahan saat menghubungi API."
                val errorResponse = ChatMessage(
                    role = "assistant",
                    content = "Error: $errorMsg",
                    isError = true
                )
                _messages.value = _messages.value + errorResponse
            }

            _isLoading.value = false
        }
    }
}
