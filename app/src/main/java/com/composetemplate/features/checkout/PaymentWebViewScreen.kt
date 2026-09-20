package com.composetemplate.features.checkout

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebViewScreen(
    url: String,
    onBack: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(padding),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val u = request?.url?.toString().orEmpty()
                            if (u.contains("/lacak-pesanan") ||
                                u.contains("/payment/return") ||
                                u.endsWith("/#akun") ||
                                u.contains("#akun")
                            ) {
                                showDialog = true
                                return true
                            }
                            return false
                        }
                    }
                    loadUrl(url)
                }
            }
        )

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Pembayaran") },
                text = { Text("Jika pembayaran sudah selesai, tap OK untuk kembali ke Beranda.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        onBack()
                    }) { Text("OK") }
                }
            )
        }
    }
}
