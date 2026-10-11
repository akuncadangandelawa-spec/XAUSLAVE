package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ChatSection
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TradingViewChart
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isTestingConnection by viewModel.isTestingConnection.collectAsState()
    val testConnectionResult by viewModel.testConnectionResult.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val splitMode by viewModel.splitMode.collectAsState()

    val animatedChartWeight by animateFloatAsState(
        targetValue = splitMode.chartWeight,
        label = "chartWeight"
    )
    val animatedChatWeight by animateFloatAsState(
        targetValue = splitMode.chatWeight,
        label = "chatWeight"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GoldPrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ShowChart,
                                    contentDescription = null,
                                    tint = DarkBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "GoldAI",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary,
                                        fontSize = 17.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Trader",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 17.sp
                                    )
                                )
                            }
                            Text(
                                text = "XAU/USD Live Chart & OpenAI AI Assistant",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Split Mode Quick Toggles
                    Row(
                        modifier = Modifier
                            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 50:50 Button
                        SplitIconButton(
                            selected = splitMode == SplitLayoutMode.BALANCED,
                            icon = Icons.Default.Splitscreen,
                            label = "50:50",
                            onClick = { viewModel.setSplitMode(SplitLayoutMode.BALANCED) }
                        )

                        // Focus Chart Button
                        SplitIconButton(
                            selected = splitMode == SplitLayoutMode.CHART_FOCUS,
                            icon = Icons.Default.ShowChart,
                            label = "Chart",
                            onClick = { viewModel.setSplitMode(SplitLayoutMode.CHART_FOCUS) }
                        )

                        // Focus Chat Button
                        SplitIconButton(
                            selected = splitMode == SplitLayoutMode.CHAT_FOCUS,
                            icon = Icons.Default.ChatBubbleOutline,
                            label = "Chat",
                            onClick = { viewModel.setSplitMode(SplitLayoutMode.CHAT_FOCUS) }
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Settings Button
                    IconButton(
                        onClick = { viewModel.openSettingsDialog() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("app_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan AI API",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            // Top Section: TradingView Chart
            TradingViewChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(animatedChartWeight),
                isExpanded = splitMode == SplitLayoutMode.CHART_FOCUS,
                onToggleExpand = { viewModel.toggleChartExpand() }
            )

            // Splitter separator handle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DarkOutline)
                        .clickable {
                            // Cycle through modes
                            val nextMode = when (splitMode) {
                                SplitLayoutMode.BALANCED -> SplitLayoutMode.CHART_FOCUS
                                SplitLayoutMode.CHART_FOCUS -> SplitLayoutMode.CHAT_FOCUS
                                SplitLayoutMode.CHAT_FOCUS -> SplitLayoutMode.BALANCED
                            }
                            viewModel.setSplitMode(nextMode)
                        }
                )
            }

            // Bottom Section: Chat Section
            ChatSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(animatedChatWeight),
                messages = messages,
                isLoading = isLoading,
                settings = settings,
                onSendMessage = { prompt -> viewModel.sendMessage(prompt) },
                onClearChat = { viewModel.clearChat() },
                onOpenSettings = { viewModel.openSettingsDialog() },
                onRetry = { viewModel.retryLast() }
            )
        }

        // Settings Dialog Modal
        if (showSettingsDialog) {
            SettingsDialog(
                currentSettings = settings,
                isTestingConnection = isTestingConnection,
                testConnectionResult = testConnectionResult,
                onDismiss = { viewModel.closeSettingsDialog() },
                onSave = { updated -> viewModel.saveSettings(updated) },
                onTestConnection = { testSettings -> viewModel.testConnection(testSettings) }
            )
        }
    }
}

@Composable
private fun SplitIconButton(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = if (selected) GoldPrimary else DarkSurfaceVariant,
        modifier = Modifier.padding(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) DarkBackground else TextSecondary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) DarkBackground else TextSecondary
                )
            )
        }
    }
}
