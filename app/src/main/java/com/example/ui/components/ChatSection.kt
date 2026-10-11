package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApiSettings
import com.example.data.model.ChatMessage
import com.example.ui.theme.AiBubble
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.RedBearish
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UserBubble
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatSection(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    settings: ApiSettings,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Auto scroll when messages change or loading
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "📈 Analisis Trend XAUUSD hari ini",
        "🎯 Support & Resistance kuat saat ini",
        "⚖️ Rekomendasi Setup Buy/Sell & SL TP",
        "📊 Kalkulasi Lot Size modal $1000 resiko 1%",
        "📰 Sentimen Fed & Dolar terhadap Emas"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chat Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground.copy(alpha = 0.85f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = GoldSecondary,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "AI Analyst Chat",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DarkOutline
                ) {
                    Text(
                        text = settings.model.ifBlank { "OpenAI" },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldPrimary,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (messages.isNotEmpty()) {
                    IconButton(
                        onClick = onClearChat,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Hapus Riwayat Chat",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Pengaturan API",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Warning if API Key is empty
            AnimatedVisibility(visible = settings.apiKey.isBlank()) {
                Surface(
                    color = GoldDark.copy(alpha = 0.15f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "API Key belum diisi.",
                            style = MaterialTheme.typography.labelSmall.copy(color = GoldPrimary),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = onOpenSettings,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = DarkSurface
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Atur API", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Chat Message List
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DarkBackground)
            ) {
                if (messages.isEmpty()) {
                    EmptyChatGreeting(
                        onSelectPrompt = { prompt ->
                            onSendMessage(prompt)
                        }
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages, key = { it.id }) { message ->
                            ChatBubble(
                                message = message,
                                onCopy = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("AI Trading Chat", message.content)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Teks disalin ke clipboard", Toast.LENGTH_SHORT).show()
                                },
                                onRetry = onRetry
                            )
                        }

                        if (isLoading) {
                            item {
                                LoadingAiBubble()
                            }
                        }
                    }
                }
            }

            // Quick Prompts Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickPrompts.forEach { prompt ->
                    Surface(
                        onClick = {
                            if (!isLoading) {
                                onSendMessage(prompt)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = DarkOutline.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Message Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Tanya analisa XAUUSD...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkOutline,
                        cursorColor = GoldPrimary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        val trimmed = inputText.trim()
                        if (trimmed.isNotBlank() && !isLoading) {
                            onSendMessage(trimmed)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isLoading) GoldPrimary else DarkOutline)
                        .testTag("send_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = DarkSurface,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim Pesan",
                            tint = if (inputText.isNotBlank()) DarkSurface else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onCopy: () -> Unit,
    onRetry: () -> Unit
) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = when {
        message.isError -> RedBearish.copy(alpha = 0.15f)
        isUser -> UserBubble
        else -> AiBubble
    }

    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
            ),
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Header inside AI bubble
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (message.isError) Icons.Default.ErrorOutline else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (message.isError) RedBearish else GoldSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (message.isError) "Gagal" else "AI Analyst",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (message.isError) RedBearish else GoldSecondary,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Salin Teks",
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Bubble Content
                SelectionContainer {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (message.isError) RedBearish else TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }

                // If error, show retry button
                if (message.isError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedBearish,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Coba Lagi", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Time footer
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 9.sp
                    ),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun LoadingAiBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = AiBubble,
        modifier = Modifier.widthIn(max = 240.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = GoldSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AI sedang menganalisis chart...",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GoldPrimary,
                    fontSize = 12.sp
                ),
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}

@Composable
fun EmptyChatGreeting(
    onSelectPrompt: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Asisten AI Trading XAU/USD",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Kombinasi live chart TradingView & AI analis pasar. Pilih topik cepat atau ketik pertanyaan trading Anda:",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
