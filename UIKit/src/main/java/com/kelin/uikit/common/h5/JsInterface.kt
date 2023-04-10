package com.kelin.uikit.common.h5

import android.app.Activity
import android.webkit.WebView

/**
 * **描述:** 定义Javascript接口规范。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/27 2:16 PM
 *
 * **版本:** v 1.0.0
 */
interface JsInterface {
    fun onInit(activity: Activity, webView: WebView)
    fun onDestroy()
}