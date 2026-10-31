package com.example.zcode

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        // 通知权限（Android 13+），允许后前台服务的通知才会显示
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true       // 关键：localStorage 存会话，重开接着用
            mediaPlaybackRequiresUserGesture = false
        }
        webView.webViewClient = WebViewClient()

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            val url = prefs().getString("url", null)
            if (url.isNullOrBlank()) showUrlDialog() else webView.loadUrl(url)
        }

        // 启动前台服务，降低被系统杀掉的概率
        val svc = Intent(this, KeepAliveService::class.java)
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(svc) else startService(svc)
    }

    private fun prefs() = getSharedPreferences("zcode", MODE_PRIVATE)

    private fun showUrlDialog() {
        val edit = EditText(this).apply {
            hint = "粘贴 zcode 移动端链接（首次配置后自动记住）"
            setSingleLine()
        }
        AlertDialog.Builder(this)
            .setTitle("配置 ZCode 连接地址")
            .setView(edit)
            .setCancelable(false)
            .setPositiveButton("保存并连接") { _, _ ->
                val url = edit.text.toString().trim()
                prefs().edit().putString("url", url).apply()
                webView.loadUrl(url)
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1, 0, "刷新")
        menu.add(0, 2, 0, "更换地址")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> webView.reload()
            2 -> showUrlDialog()
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()   // 后台时暂停网页，省流量省电
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()  // 回到前台自动恢复页面（会话还在，无需重新登录）
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
