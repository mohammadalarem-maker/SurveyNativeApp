package com.alsarem.survey

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        // إعدادات الـ WebView لتشغيل الأزرار والخريطة
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.databaseEnabled = true
        
        // منع فتح الروابط في متصفح خارجي
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // تحميل ملف مشروعك الأساسي
        webView.loadUrl("file:///android_asset/index.html")
    }
}
