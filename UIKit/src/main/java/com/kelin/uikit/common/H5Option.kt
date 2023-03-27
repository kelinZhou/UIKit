package com.kelin.uikit.common

import android.content.Intent
import com.kelin.uikit.R
import com.kelin.uikit.common.Option.Companion.KEY_NAVIGATION_ICON
import com.kelin.uikit.common.h5.JavascriptInterfaceWrapper
import com.kelin.uikit.common.h5.JsInterface


/**
 * **描述:** H5页面的配置。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/21 11:34 AM
 *
 * **版本:** v 1.0.0
 */
interface H5Option : Option {

    companion object {
        internal const val KEY_INTENT_URL = "key_intent_url"
        internal const val KEY_HTML_CONTENT = "key_html_content"
        internal const val KEY_JAVASCRIPT_INTERFACE = "key_javascript_interface"
        internal const val KEY_H5_CLOSE_STYLE = "key_h5_close_style"
        internal const val KEY_STATUS_BAR_DARK = "key_status_bar_dark"

        internal fun getH5Url(intent: Intent): String? {
            return intent.getStringExtra(KEY_INTENT_URL)
        }

        internal fun getH5Data(intent: Intent): String? {
            return intent.getStringExtra(KEY_HTML_CONTENT)
        }

        internal fun getJavascriptInterface(intent: Intent): JavascriptInterfaceWrapper? {
            return intent.getSerializableExtra(KEY_JAVASCRIPT_INTERFACE) as? JavascriptInterfaceWrapper
        }

        internal fun isCloseStyle(intent: Intent): Boolean {
            return intent.getBooleanExtra(KEY_H5_CLOSE_STYLE, false)
        }

        internal fun isStatusBarDark(intent: Intent): Boolean {
            return intent.getBooleanExtra(KEY_STATUS_BAR_DARK, false)
        }
    }

    /**
     * 设置Cookie。
     */
    fun cookie(cookie: String)

    /**
     * 通过浏览器打开当前Url链接。
     * @param optional 默认是通过手机自带浏览器打开的，如需设置指定浏览器可通过配置回调自行设置。
     */
    fun byBrowser(optional: (Intent.() -> Unit)? = null)

    /**
     * 设置状态颜色模式。
     * @param dark true表示为状态栏底色为深色，状态栏文字颜色为白色。false表示状态栏文字颜色为黑色。默认为false。
     */
    fun statusBarDark(dark: Boolean) {
        intent.putExtra(KEY_STATUS_BAR_DARK, dark)
    }

    /**
     * 设置页面为关闭样式。
     * @param navigationIcon 页面返回按钮的样式。调用该方法后immersionToolbar方法中设置的页面返回按钮样式将不在生效，所以需要在此方法中设置页面返回按钮的样式。
     * @see immersionToolbar
     */
    fun closeStyle(navigationIcon: Int? = null) {
        intent.putExtra(KEY_H5_CLOSE_STYLE, true)
        intent.putExtra(KEY_NAVIGATION_ICON, navigationIcon ?: R.drawable.ic_navigation_close_dark)
    }

    override fun navigationIcon(icon: Int) {
        //防止设置closeStyle后再调用该方法会覆盖closeStyle中navigationIcon的设置。
        if (!isCloseStyle(intent)) {
            super.navigationIcon(icon)
        }
    }

    /**
     * 设置Javascript接口。
     * @param interfaceClass 接口类型，必须提供无参构造。
     * @param interfaceName 接口名称。
     */
    fun setJavascriptInterface(interfaceClass: Class<out JsInterface>, interfaceName: String) {
        intent.putExtra(KEY_JAVASCRIPT_INTERFACE, JavascriptInterfaceWrapper(interfaceName, interfaceClass))
    }
}

/**
 * 添加Javascript接口。
 * @param F 接口类型，必须提供无参构造。
 * @param interfaceName 接口名称。
 */
inline fun <reified F : JsInterface> H5Option.setJavascriptInterface(interfaceName: String) {
    setJavascriptInterface(F::class.java, interfaceName)
}