package com.kelin.uikit.common

import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.kelin.uikit.BasicFragment
import com.kelin.uikit.getString

/**
 * **描述:** 页面启动的配置项。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/21 10:43 AM
 *
 * **版本:** v 1.0.0
 */
interface Option {
    companion object {
        private const val KEY_IMMERSION_MODE = "key_immersion_mode"
        internal const val KEY_NAVIGATION_ICON = "key_navigation_icon"
        internal const val KEY_NAVIGATION_TEXT = "key_navigation_text"
        internal const val KEY_TOOL_BAT_BG = "key_tool_bat_bg"
        internal const val KEY_PAGE_MODE = "key_page_mode"
        internal const val KEY_PAGE_TITLE = "key_page_title"
        internal const val KEY_TARGET_PAGE = "key_target_fragment_class"

        @Suppress("UNCHECKED_CAST")
        internal fun getTargetFragment(intent: Intent): Fragment? {
            return (intent.getSerializableExtra(KEY_TARGET_PAGE) as? Class<out Fragment>)?.let {
                BasicFragment.newInstance(it, intent.extras)
            }
        }

        /**
         * 获取页面类型。
         */
        internal fun getPageMode(intent: Intent): PageMode {
            return intent.getSerializableExtra(KEY_PAGE_MODE) as PageMode
        }

        internal fun getImmersionMode(intent: Intent): ImmersionMode {
            return (intent.getSerializableExtra(KEY_IMMERSION_MODE) as? ImmersionMode) ?: ImmersionMode.NONE
        }

        @ColorInt
        internal fun getToolbarBg(intent: Intent): Int? {
            return intent.getIntExtra(KEY_TOOL_BAT_BG, -1).takeIf { it >= 0 }
        }
    }

    val intent: Intent

    /**
     * 为新的页面设置沉浸式样式，设置沉浸式样式后在非Tab样式下会隐藏默认的Toolbar，如希望显示Toolbar请调用immersionToolbar方法设置沉浸式样式。
     * @see immersionToolbar
     */
    fun immersion() {
        intent.putExtra(KEY_IMMERSION_MODE, ImmersionMode.NO_TOOLBAR)
    }

    /**
     * 为新的页面设置沉浸式样式，设置沉浸式样式后Toolbar会盖在目标页面上，需要为目标页面设置一定的顶部间距保证Toolbar不会遮挡到目标页面的内容。
     * 如不想使用Toolbar则需要调用immersion方法设置沉浸式样式。
     * @see immersion
     */
    fun immersionToolbar() {
        intent.putExtra(KEY_IMMERSION_MODE, ImmersionMode.HAVE_TOOLBAR)
    }

    /**
     * 设置页面返回按钮样式。
     * @param icon 页面返回按钮的ICON。
     */
    fun navigationIcon(icon: Int) {
        if (getImmersionMode(intent) != ImmersionMode.NO_TOOLBAR) {
            intent.putExtra(KEY_NAVIGATION_ICON, icon)
        }
    }

    /**
     * 设置页面返回按钮为文字样式。
     */
    fun navigationText(@StringRes text: Int) {
        navigationText(getString(text))
    }

    /**
     * 设置页面返回按钮为文字样式。
     */
    fun navigationText(text: CharSequence) {
        if (getImmersionMode(intent) != ImmersionMode.NO_TOOLBAR) {
            intent.putExtra(KEY_NAVIGATION_TEXT, text)
        }
    }

    /**
     * 工具栏背景色。
     */
    fun toolbarBgRes(@ColorRes color: Int)

    /**
     * 工具栏背景色。
     */
    fun toolbarBg(@ColorInt color: Int)


    /**
     * Activity的启动配置。
     * @param options 配置内容。
     */
    fun options(options: Bundle?)

    /**
     * Activity的启动配置。
     * @param options 配置内容。
     */
    fun options(options: Bundle.() -> Unit)

    /**
     * 设置返回结果回调。
     * @param callback 回调函数。
     */
    fun <D> results(callback: ((data: D?) -> Unit)?)


    /**
     * 设置返回结果回调。
     * @param callback 回调函数。
     */
    fun resultsForCode(callback: ((resultCode: Int) -> Unit)?)
}

/**
 * 沉浸模式。
 */
internal enum class ImmersionMode {

    /**
     * 不开启。
     */
    NONE,

    /**
     * 没有Toolbar的。
     */
    NO_TOOLBAR,

    /**
     * 有Toolbar的。
     */
    HAVE_TOOLBAR;
}