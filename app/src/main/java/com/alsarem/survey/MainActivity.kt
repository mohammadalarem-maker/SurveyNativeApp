package com.alsarem.survey

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.GeolocationPermissions
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        // إعدادات الـ WebView المتقدمة لتشغيل الخريطة والأزرار
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.databaseEnabled = true
        
        // السماح بتحميل البيانات المختلطة لضمان جلب مربعات الخريطة (Tiles) عبر الإنترنت
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        // منع فتح الروابط في متصفح خارجي
        webView.webViewClient = WebViewClient()

        // تمرير صلاحيات الـ GPS من النظام إلى داخل الخريطة مباشرة لمنع تجمد الشاشة
        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                // الموافقة التلقائية لملف الخريطة للوصول إلى نظام الإحداثيات
                callback?.invoke(origin, true, false)
            }
        }

        // تحميل ملف مشروعك الأساسي
        webView.loadUrl("file:///android_asset/index.html")
    }
}
