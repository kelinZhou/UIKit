package com.kelin.uikit.common.h5

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * **描述:** H5页面的代理。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/27 11:28 AM
 *
 * **版本:** v 1.0.0
 */
internal class H5Delegate(
    owner: LifecycleOwner,
    private val webView: WebView,
    private val closeStyle: Boolean,
    private val stateView: View,
    javascriptInterface: JavascriptInterfaceWrapper?,
    private val onReceivedErrorCallback: (view: WebView, request: WebResourceRequest, error: WebResourceError) -> Unit
) : LifecycleEventObserver {
    private var jsInterface: JsInterface? = null

    init {
        owner.lifecycle.addObserver(this)
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }
        webView.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                super.onReceivedSslError(view, handler, error)
                handler.proceed()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    //对http或者https协议的链接进行加载
                    view.loadUrl(url)
                } else {
                    //这里需要捕捉异常，因为如果没有安装相关的APP会有类找不到的异常
                    try {
                        //启动对应协议的APP
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        view.context.startActivity(intent)
                    } catch (e: Exception) {
                    }
                }
                return true
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && error.errorCode == ERROR_TIMEOUT) {
                    stateView.visibility = View.VISIBLE
                    onReceivedErrorCallback(view, request, error)
                }
                super.onReceivedError(view, request, error)
            }
        }
        webView.settings.run {
            setAppCacheEnabled(false)
            cacheMode = WebSettings.LOAD_NO_CACHE
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            allowFileAccess = true
            blockNetworkImage = false
            pluginState = WebSettings.PluginState.ON
            setSupportZoom(true)
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            defaultZoom = WebSettings.ZoomDensity.CLOSE
            databaseEnabled = true
            defaultTextEncodingName = "utf-8"
            loadsImagesAutomatically = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            allowContentAccess = true
            userAgentString = "${userAgentString}; User-Agent:Android"
        }
        javascriptInterface?.also { initJavascriptInterface(it, owner as Activity) }
    }

    @SuppressLint("JavascriptInterface")
    fun initJavascriptInterface(javascriptInterface: JavascriptInterfaceWrapper, activity: Activity) {
        jsInterface = javascriptInterface.interfaceClass.newInstance().apply { onInit(activity, webView) }
        webView.addJavascriptInterface(jsInterface!!, javascriptInterface.interfaceName)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                webView.onPause()
            }
            Lifecycle.Event.ON_RESUME -> {
                webView.onResume()
            }
            Lifecycle.Event.ON_DESTROY -> {
                jsInterface?.onDestroy()
                jsInterface = null
            }
            else -> {
            }
        }
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (!closeStyle && event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()//返回上一页面
            true
        } else {
            false
        }
    }

    fun onPackPressed(): Boolean {
        //处理closeableStyle
        return if (!closeStyle && webView.canGoBack()) {
            webView.goBack()//返回上一页面
            true
        } else {
            false
        }
    }
}