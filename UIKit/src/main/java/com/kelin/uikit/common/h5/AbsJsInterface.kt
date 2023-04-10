package com.kelin.uikit.common.h5

import android.app.Activity
import android.webkit.WebView

/**
 * **描述:** Javascript接口的基本实现。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/27 2:21 PM
 *
 * **版本:** v 1.0.0
 */
abstract class AbsJsInterface : JsInterface {
    protected var activity: Activity? = null
        private set

    protected var webView: WebView? = null

    protected fun requireActivity(): Activity {
        return activity ?: throw NullPointerException("The activity is null early!")
    }

    protected fun requireWebView(): WebView {
        return webView ?: throw NullPointerException("The webView is null early!")
    }

    override fun onInit(activity: Activity, webView: WebView) {
        this.activity = activity
        this.webView = webView
    }

    override fun onDestroy() {
        this.activity = null
        this.webView = null
    }
}