package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApiSettings
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenBullish
import com.example.ui.theme.RedBearish
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    currentSettings: ApiSettings,
    isTestingConnection: Boolean,
    testConnectionResult: Pair<Boolean, String>?,
    onDismiss: () -> Unit,
    onSave: (ApiSettings) -> Unit,
    onTestConnection: (ApiSettings) -> Unit
) {
    var baseUrl by remember { mutableStateOf(currentSettings.baseUrl) }
    var apiKey by remember { mutableStateOf(currentSettings.apiKey) }
    var model by remember { mutableStateOf(currentSettings.model) }
    var systemPrompt by remember { mutableStateOf(currentSettings.systemPrompt) }
    var temperature by remember { mutableFloatStateOf(currentSettings.temperature) }
    var showApiKey by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pengaturan OpenAI API",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
                Text(
                    text = "Tersimpan otomatis via Jetpack DataStore",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldDark,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(start = 30.dp, top = 2.dp)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Preset Provider Populer:",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                )

                // Provider Presets Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = baseUrl.contains("api.openai.com"),
                        onClick = {
                            baseUrl = "https://api.openai.com/v1"
                            model = "gpt-4o-mini"
                        },
                        label = { Text("OpenAI") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark.copy(alpha = 0.3f),
                            selectedLabelColor = GoldPrimary
                        )
                    )

                    FilterChip(
                        selected = baseUrl.contains("deepseek.com"),
                        onClick = {
                            baseUrl = "https://api.deepseek.com/v1"
                            model = "deepseek-chat"
                        },
                        label = { Text("DeepSeek") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark.copy(alpha = 0.3f),
                            selectedLabelColor = GoldPrimary
                        )
                    )

                    FilterChip(
                        selected = baseUrl.contains("openrouter.ai"),
                        onClick = {
                            baseUrl = "https://openrouter.ai/api/v1"
                            model = "openai/gpt-4o-mini"
                        },
                        label = { Text("OpenRouter") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark.copy(alpha = 0.3f),
                            selectedLabelColor = GoldPrimary
                        )
                    )

                    FilterChip(
                        selected = baseUrl.contains("groq.com"),
                        onClick = {
                            baseUrl = "https://api.groq.com/openai/v1"
                            model = "llama-3.3-70b-versatile"
                        },
                        label = { Text("Groq") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark.copy(alpha = 0.3f),
                            selectedLabelColor = GoldPrimary
                        )
                    )

                    FilterChip(
                        selected = baseUrl.contains("11434") || baseUrl.contains("1234"),
                        onClick = {
                            baseUrl = "http://10.0.2.2:11434/v1"
                            model = "llama3"
                        },
                        label = { Text("Ollama Local") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark.copy(alpha = 0.3f),
                            selectedLabelColor = GoldPrimary
                        )
                    )
                }

                // Custom API Base URL
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    label = { Text("Custom API Base URL") },
                    placeholder = { Text("https://api.openai.com/v1") },
                    leadingIcon = {
                        Icon(Icons.Default.Link, contentDescription = null, tint = GoldPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_base_url"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedLabelColor = GoldPrimary,
                        cursorColor = GoldPrimary
                    )
                )

                // API Key Field
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    placeholder = { Text("sk-...") },
                    leadingIcon = {
                        Icon(Icons.Default.Key, contentDescription = null, tint = GoldPrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showApiKey) "Sembunyikan Key" else "Tampilkan Key",
                                tint = TextSecondary
                            )
                        }
                    },
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_api_key"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedLabelColor = GoldPrimary,
                        cursorColor = GoldPrimary
                    )
                )

                // Model Field
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Nama Model") },
                    placeholder = { Text("gpt-4o-mini / deepseek-chat / dll.") },
                    leadingIcon = {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = GoldPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_model"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedLabelColor = GoldPrimary,
                        cursorColor = GoldPrimary
                    )
                )

                // Model Quick Suggestion Chips
                Text(
                    text = "Saran Model Cepat:",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("gpt-4o-mini", "gpt-4o", "deepseek-chat", "llama-3.3-70b-versatile", "claude-3-5-sonnet").forEach { m ->
                        Surface(
                            onClick = { model = m },
                            shape = RoundedCornerShape(12.dp),
                            color = if (model == m) GoldDark.copy(alpha = 0.3f) else DarkSurfaceVariant,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = m,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (model == m) GoldPrimary else TextSecondary,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Temperature Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kreativitas / Temperature:",
                            style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                        )
                        Text(
                            text = String.format("%.2f", temperature),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Slider(
                        value = temperature,
                        onValueChange = { temperature = it },
                        valueRange = 0f..1.2f,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = DarkOutline
                        )
                    )
                }

                // System Prompt
                OutlinedTextField(
                    value = systemPrompt,
                    onValueChange = { systemPrompt = it },
                    label = { Text("System Prompt (Instruksi Perilaku AI)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedLabelColor = GoldPrimary,
                        cursorColor = GoldPrimary
                    )
                )

                // Test Connection Button & Result
                OutlinedButton(
                    onClick = {
                        val testSetting = ApiSettings(
                            baseUrl = baseUrl,
                            apiKey = apiKey,
                            model = model,
                            systemPrompt = systemPrompt,
                            temperature = temperature
                        )
                        onTestConnection(testSetting)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_connection_button"),
                    enabled = !isTestingConnection
                ) {
                    if (isTestingConnection) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Menguji Koneksi...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Uji Koneksi Endpoint AI", color = GoldPrimary)
                    }
                }

                // Connection Test Feedback Box
                testConnectionResult?.let { (success, msg) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (success) GreenBullish.copy(alpha = 0.15f) else RedBearish.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (success) GreenBullish else RedBearish,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (success) GreenBullish else RedBearish,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = ApiSettings(
                        baseUrl = baseUrl,
                        apiKey = apiKey,
                        model = model,
                        systemPrompt = systemPrompt,
                        temperature = temperature
                    )
                    android.widget.Toast.makeText(context, "Pengaturan disimpan ke DataStore", android.widget.Toast.LENGTH_SHORT).show()
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = DarkSurface
                ),
                modifier = Modifier.testTag("save_settings_button")
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
