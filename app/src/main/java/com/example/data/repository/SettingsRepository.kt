package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.ApiSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.apiSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "openai_api_settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val BASE_URL = stringPreferencesKey("custom_openai_base_url")
        val API_KEY = stringPreferencesKey("custom_openai_api_key")
        val MODEL = stringPreferencesKey("custom_openai_model")
        val SYSTEM_PROMPT = stringPreferencesKey("custom_openai_system_prompt")
        val TEMPERATURE = floatPreferencesKey("custom_openai_temperature")
    }

    val settingsFlow: Flow<ApiSettings> = context.apiSettingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            ApiSettings(
                baseUrl = preferences[PreferencesKeys.BASE_URL] ?: "https://api.openai.com/v1",
                apiKey = preferences[PreferencesKeys.API_KEY] ?: "",
                model = preferences[PreferencesKeys.MODEL] ?: "gpt-4o-mini",
                systemPrompt = preferences[PreferencesKeys.SYSTEM_PROMPT] ?: ApiSettings.DEFAULT_SYSTEM_PROMPT,
                temperature = preferences[PreferencesKeys.TEMPERATURE] ?: 0.7f
            )
        }

    suspend fun saveSettings(settings: ApiSettings) {
        context.apiSettingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.BASE_URL] = settings.baseUrl.trim().removeSuffix("/")
            preferences[PreferencesKeys.API_KEY] = settings.apiKey.trim()
            preferences[PreferencesKeys.MODEL] = settings.model.trim()
            preferences[PreferencesKeys.SYSTEM_PROMPT] = settings.systemPrompt.trim()
            preferences[PreferencesKeys.TEMPERATURE] = settings.temperature
        }
    }
}
