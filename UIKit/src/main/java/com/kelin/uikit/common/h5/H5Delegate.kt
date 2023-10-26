package com.kelin.uikit.common.h5

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.kelin.logger.Logger

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
    private val progressView: ProgressBar,
    private val closeStyle: Boolean,
    private val stateView: View,
    javascriptInterface: JavascriptInterfaceWrapper?,
    private val onReceivedErrorCallback: (view: WebView, request: WebResourceRequest, error: WebResourceError) -> Unit
) : LifecycleEventObserver {

    companion object{
        private const val TAG = "H5Delegate"
    }

    private var homePagePath: String? = null
    private var jsInterface: JsInterface? = null

    /**
     * 当前页面Path。
     */
    private val currentPagePath: String?
        get() = webView.url?.let {
            if (it.contains("?")) {
                it.split("?").first()
            } else {
                it
            }
        }

    init {
        owner.lifecycle.addObserver(this)
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressView.run {
                    progress = newProgress
                    visibility = if (newProgress == 100) {
                        if (homePagePath == null) {
                            homePagePath = currentPagePath
                            Logger.system(TAG)?.i("页面加载完毕，当前Path:${homePagePath}")
                        }
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
            }
        }
        webView.webViewClient = object : WebViewClient() {
            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                super.onReceivedSslError(view, handler, error)
                handler.proceed()
            }

            @Deprecated("Deprecated in Java")
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
                        e.printStackTrace()
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

    fun onPackPressed(): Boolean {
        //处理closeableStyle
        return if (!closeStyle && currentPagePath != homePagePath) {
            Logger.system(TAG)?.i("拦截页面返回，当前Path:${homePagePath}")
            webView.goBack()//返回上一页面
            true
        } else {
            Logger.system(TAG)?.i("当前为首页，不拦截页面返回，退出H5页面。Path:${homePagePath}")
            homePagePath = null
            false
        }
    }
}