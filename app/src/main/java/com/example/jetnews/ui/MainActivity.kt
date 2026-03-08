package com.example.jetnews.ui // <-- UPDATE THIS to match your package structure

// Warning: Slop Ahead

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Alignment
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        setContent {
            MainScreen()
        }
    }
}

enum class AppTab { infor, ipc4me, settings }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val prefsHelper = remember { PrefsHelper(context) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf(AppTab.infor) }

    val tabs = AppTab.values()
    val currentIndex = currentTab.ordinal

    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    BackHandler(enabled = webViewRef?.canGoBack() == true) {
        webViewRef?.goBack()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false, 
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "IPC Portal",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()

                tabs.forEach { tab ->
                    NavigationDrawerItem(
                        label = { Text(tab.name.uppercase()) },
                        selected = currentTab == tab,
                        onClick = {
                            currentTab = tab
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    tonalElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        
                        IconButton(onClick = { webViewRef?.reload() }) {
                            Text("↻", fontSize = 22.sp)
                        }

                        IconButton(
                            onClick = {
                                val user = prefsHelper.getUsername()
                                val pass = prefsHelper.getPassword()
                                
                                if (user.isNotEmpty() && pass.isNotEmpty()) {
                                    // JavaScript to find the fields, fill them, and trigger Angular's state update
                                    val jsInjection = """
                                        (function() {
                                            var u = document.querySelector('input[type="text"], input[type="email"], input[name*="user"], input[id*="user"]');
                                            var p = document.querySelector('input[type="password"]');
                                            
                                            if (u && p) {
                                                u.value = '$user';
                                                u.dispatchEvent(new Event('input', { bubbles: true }));
                                                
                                                p.value = '$pass';
                                                p.dispatchEvent(new Event('input', { bubbles: true }));
                                            }
                                        })();
                                    """.trimIndent()
                                    
                                    webViewRef?.evaluateJavascript(jsInjection, null)
                                }
                            }
                        ) {
                            Text("⚷", fontSize = 22.sp)
                        }


                        Spacer(Modifier.weight(1f))

                        IconButton(
                            onClick = {
                                val prevIndex = if (currentIndex == 0) tabs.size - 1 else currentIndex - 1
                                currentTab = tabs[prevIndex]
                            }
                        ) {
                            Text("◀", fontSize = 22.sp)
                        }

                        // 4. Right Arrow Button (Next Tab)
                        IconButton(
                            onClick = {
                                val nextIndex = if (currentIndex == tabs.size - 1) 0 else currentIndex + 1
                                currentTab = tabs[nextIndex]
                            }
                        ) {
                            Text("▶", fontSize = 22.sp)
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (currentTab) {
                    AppTab.infor -> CustomWebView(
                        url = "https://inforestudante.ipc.pt",
                        onCreated = { webViewRef = it }
                    )

                    AppTab.ipc4me -> CustomWebView(
                        url = "https://ipc4me.ipc.pt",
                        onCreated = { webViewRef = it }
                    )

                    AppTab.settings -> {
                        LaunchedEffect(Unit) {
                            webViewRef = null
                        }
                        ConfigurationScreen()
                    }
                }
            }
        }
    }
}
