package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenBullish
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TradingViewChart(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit,
    chartUrl: String = "https://www.tradingview.com/chart/hZrtXwQG/?symbol=OANDA%3AXAUUSD"
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chart Header Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground.copy(alpha = 0.85f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "XAU/USD",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = GoldDark.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "OANDA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Live status dot
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(GreenBullish)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "LIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GreenBullish,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons: Reload, Open in browser, Expand/Collapse
                IconButton(
                    onClick = {
                        webViewInstance?.reload()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Muat Ulang Chart",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(chartUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Buka di Browser",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = if (isExpanded) "Perkecil Chart" else "Perbesar Chart",
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Loading indicator
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = GoldPrimary,
                    trackColor = DarkBackground,
                )
            }

            // WebView Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DarkBackground)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    progress = newProgress / 100f
                                    isLoading = newProgress < 100
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    // Keep navigation within webview if inside TradingView
                                    val url = request?.url?.toString() ?: return false
                                    if (url.contains("tradingview.com")) {
                                        return false
                                    }
                                    return try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        ctx.startActivity(intent)
                                        true
                                    } catch (_: Exception) {
                                        false
                                    }
                                }
                            }

                            loadUrl(chartUrl)
                            webViewInstance = this
                        }
                    },
                    update = {
                        // Keep instance updated
                        webViewInstance = it
                    }
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.destroy()
        }
    }
}
