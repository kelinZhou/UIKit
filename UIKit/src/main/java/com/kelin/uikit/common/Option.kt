package com.kelin.uikit.common

import android.content.Intent
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.kelin.okpermission.OkActivityResult
import com.kelin.uikit.BasicFragment

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
    }

    val launchOptions: Bundle?

    val intent: Intent

    /**
     * 为新的页面设置沉浸式样式，设置沉浸式样式后在非Tab样式下会隐藏默认的Toolbar，如希望显示Toolbar请调用immersionToolbar方法设置沉浸式样式。
     * @see immersionToolbar
     */
    fun immersion() {
        intent.putExtra(ImmersionMode.KEY_IMMERSION_MODE, ImmersionMode.NO_TOOLBAR)
    }

    /**
     * 为新的页面设置沉浸式样式，设置沉浸式样式后Toolbar会盖在目标页面上，需要为目标页面设置一定的顶部间距保证Toolbar不会遮挡到目标页面的内容。
     * 如不想使用Toolbar则需要调用immersion方法设置沉浸式样式。
     * @see immersion
     */
    fun immersionToolbar(@DrawableRes navigationIcon: Int? = null) {
        intent.putExtra(ImmersionMode.KEY_IMMERSION_MODE, ImmersionMode.HAVE_TOOLBAR)
        intent.putExtra(ImmersionMode.KEY_NAVIGATION_ICON, navigationIcon)
    }

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

    companion object {
        internal const val KEY_IMMERSION_MODE = "key_immersion_mode"
        internal const val KEY_NAVIGATION_ICON = "key_navigation_icon"
    }
}


/**
 * 启动页面。
 * @param onResult 如果需要从目标页面获取结果则需要传入回调。
 */
fun Option.start(onResult: ((resultCode: Int) -> Unit)? = null) {
    (this as? AbsOption)?.run {
        if (onResult != null && isFromActivityContext) {
            OkActivityResult.startActivity(useActivity, intent, launchOptions, onResult)
        } else {
            useContext.startActivity(intent, launchOptions)
        }
    }
}

/**
 * 启动页面。
 * @param onResult 如果需要从目标页面获取结果则需要传入回调。
 */
fun <D> Option.start(onResult: ((resultCode: Int, data: D?) -> Unit)?) {
    (this as? AbsOption)?.run {
        if (onResult != null && isFromActivityContext) {
            OkActivityResult.startActivity(useActivity, intent, launchOptions, onResult)
        } else {
            useContext.startActivity(intent, launchOptions)
        }
    }
}