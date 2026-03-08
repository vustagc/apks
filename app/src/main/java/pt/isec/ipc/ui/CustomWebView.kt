package pt.isec.ipc.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CustomWebView(
    url: String,
    domain: String,
    onCreated: (WebView) -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->

            WebView(context).apply {

                onCreated(this)

                setBackgroundColor(0)
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                    userAgentString =
                        "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        isAlgorithmicDarkeningAllowed = false
                    }
                }

                webViewClient = object : WebViewClient() {

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {

                        val targetUrl = request?.url.toString()

                        // Block navigation outside the allowed domain
                        if (!targetUrl.contains(domain)) {

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
                            context.startActivity(intent)

                            return true
                        }

                        return false
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                    }
                }

                // Download support
                setDownloadListener { downloadUrl, userAgent, contentDisposition, mimeType, _ ->

                    val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {

                        setMimeType(mimeType)

                        val cookies = CookieManager.getInstance().getCookie(downloadUrl)
                        addRequestHeader("cookie", cookies)
                        addRequestHeader("User-Agent", userAgent)

                        setDescription("Downloading file...")
                        setTitle(URLUtil.guessFileName(downloadUrl, contentDisposition, mimeType))

                        setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                        )

                        setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            URLUtil.guessFileName(downloadUrl, contentDisposition, mimeType)
                        )
                    }

                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(request)

                    Toast.makeText(context, "Downloading file...", Toast.LENGTH_LONG).show()
                }

                loadUrl(url)
            }
        },

        update = { webView ->
            if (webView.url != url && webView.url != null) {
                webView.loadUrl(url)
            }
        }
    )
}
