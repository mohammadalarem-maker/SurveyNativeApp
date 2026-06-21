package com.alsarem.survey

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.setGeolocationEnabled(true) // تفعيل الموقع
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // السماح بالاتصال المفتوح

        // ربط واجهة الجافا سكريبت بالأندرويد لتمرير الصورة
        webView.addJavascriptInterface(WebAppInterface(this), "AndroidInterface")

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
                callback.invoke(origin, true, false)
            }
        }

        // طلب صلاحيات الموقع من النظام
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        webView.loadUrl("file:///android_asset/index.html")
    }
}

class WebAppInterface(private val activity: MainActivity) {
    @JavascriptInterface
    fun saveImage(base64Data: String) {
        try {
            // تنظيف بيانات الـ Base64 واستخراج الصورة
            val pureBase64 = base64Data.substringAfter(",")
            val imageBytes = Base64.decode(pureBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            val filename = "Blueprint_${System.currentTimeMillis()}.png"
            val contentResolver = activity.contentResolver

            // إعدادات الحفظ المتوافقة مع أندرويد 10 فما فوق وتحت 10
            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SurveyApp")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val imageUri = contentResolver.insert(imageCollection, contentValues)
            if (imageUri != null) {
                val outputStream: OutputStream? = contentResolver.openOutputStream(imageUri)
                outputStream?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(imageUri, contentValues, null, null)
                }

                activity.runOnUiThread {
                    Toast.makeText(activity, "تم حفظ المخطط في المعرض بنجاح! 💾", Toast.LENGTH_LONG).show()
                }
            } else {
                throw Exception("تعذر إنشاء سجل في معرّف الصور")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            activity.runOnUiThread {
                Toast.makeText(activity, "فشل حفظ الصورة: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
