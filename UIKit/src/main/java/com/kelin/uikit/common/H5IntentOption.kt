package com.kelin.uikit.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.kelin.uikit.common.H5Option.Companion.KEY_HTML_CONTENT
import com.kelin.uikit.common.H5Option.Companion.KEY_INTENT_URL
import com.kelin.uikit.widget.optionsmenu.exception.IllegalCalledException

/**
 * **描述:** Tab页面配置的具体实现。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/21 11:39 AM
 *
 * **版本:** v 1.0.0
 */
class H5IntentOption(context: Context) : AbsOption(context), H5Option {

    init {
        setPageMode(PageMode.H5)
    }

    private var byBrowserIntent: Intent? = null

    override val isH5ByBrowser: Boolean
        get() = byBrowserIntent != null

    override fun setTarget(target: Class<out Fragment>) {
        throw IllegalCalledException("Can't set target Fragment with h5 page !")
    }

    /**
     * 为新的页面设置标题，仅在没有调用immersion或immersionToolbar方法时生效。
     * @param title 页面的标题。
     * @param color 页面标题的颜色。
     */
    fun title(title: CharSequence, @ColorInt color: Int?) {
        intent.putExtra(IOption.KEY_PAGE_TITLE, title)
        intent.putExtra(IOption.KEY_PAGE_TITLE_CENTER, color)
    }

    /**
     * 为新的页面设置标题是否居中显示，仅在没有调用immersion或immersionToolbar方法时生效。
     * @param center 标题是否居中显示。
     * @see immersion
     * @see immersionToolbar
     */
    override fun titleCenter(center: Boolean) {
        intent.putExtra(IOption.KEY_PAGE_TITLE_CENTER, center)
    }

    var h5url: String = ""
        set(value) {
            field = value
            intent.putExtra(KEY_INTENT_URL, value)
        }

    var h5Data: String? = null
        set(value) {
            field = value
            intent.putExtra(KEY_HTML_CONTENT, value)
        }

    override fun cookie(cookie: String) {
        h5url.takeIf { it.isNotBlank() }?.also { url ->
            context?.also {
                syncCookie(it, url, cookie)
            }
        } ?: throw NullPointerException("You must launch with url!")
    }

    override fun byBrowser(optional: (Intent.() -> Unit)?) {
        h5url.takeIf { it.isNotBlank() }?.also { url ->
            byBrowserIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
                addCategory(Intent.CATEGORY_BROWSABLE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                optional?.invoke(this)
            }
        } ?: throw NullPointerException("You must launch with url!")
    }

    override fun start() {
        try {
            useContext.startActivity(byBrowserIntent ?: intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun syncCookie(context: Context, url: String, cookie: String) {
        synCookies(context, url, "_caelusToken=$cookie")
        synCookies(context, url, "_app=Android")
    }

    private fun synCookies(context: Context, url: String, cookie: String) {
        val csm = CookieSyncManager.createInstance(context)
        val cm = CookieManager.getInstance()
        cm.setAcceptCookie(true)
        cm.setCookie(url, cookie)//cookies是在HttpClient中获得的cookie

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            cm.flush()
        } else {
            csm.sync()
        }
    }
}