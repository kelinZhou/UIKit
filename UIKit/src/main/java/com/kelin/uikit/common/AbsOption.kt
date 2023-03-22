package com.kelin.uikit.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * **描述:** 页面启动的配置项。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/21 1:40 PM
 *
 * **版本:** v 1.0.0
 */
abstract class AbsOption(context: Context) {
    val intent = Intent(context, Navigation::class.java)
    private var mLaunchOptions: Bundle? = null

    val launchOptions: Bundle?
        get() = mLaunchOptions

    private var context: Context? = context

    internal val isFromActivityContext: Boolean
        get() = context is Activity

    internal val useActivity: Activity
        get() = useContext as Activity

    internal val useContext: Context
        get() {
            val contextTemp = context
            context = null
            return contextTemp ?: throw NullPointerException("The context used early!")
        }

    fun setPageMode(mode: PageMode) {
        intent.putExtra(Option.KEY_PAGE_MODE, mode)
    }

    open fun setTarget(target: Class<out Fragment>) {
        intent.putExtra(Option.KEY_TARGET_PAGE, target)
    }

    /**
     * Activity的启动配置。
     * @param options 配置内容。
     */
    fun options(options: Bundle?) {
        mLaunchOptions = options
    }

    /**
     * Activity的启动配置。
     * @param options 配置内容。
     */
    fun options(options: Bundle.() -> Unit) {
        mLaunchOptions = Bundle().apply { options(this) }
    }
}

enum class PageMode {
    /**
     * 普通的。
     */
    NORMAL,

    /**
     * TabLayout+ViewPager。
     */
    TAB,

    /**
     * 搜索。
     */
    SEARCH
}