package com.alsarem.survey

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val webView = WebView(this)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            builtInZoomControls = true
            displayZoomControls = false
            databaseEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        
        // استدعاء ملف الويب من أصول مشروع النيتف الداخلي
        webView.loadUrl("file:///android_asset/index.html")
        setContentView(webView)
    }
}
