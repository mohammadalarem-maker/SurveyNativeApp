package com.alsarem.survey

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.GeolocationPermissions
import android.webkit.ConsoleMessage
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        // إعدادات تشغيل الجافا سكريبت والخريطة
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.databaseEnabled = true
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback?.invoke(origin, true, false)
            }

            // اقتناص أخطاء الخريطة والجافا سكريبت وطباعتها في لوج الأندرويد
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.e("MAP_ERROR", "[JS_Crash] ${consoleMessage?.message()} -- Line: ${consoleMessage?.lineNumber()} in ${consoleMessage?.sourceId()}")
                return true
            }
        }

        webView.loadUrl("file:///android_asset/index.html")
    }
}
