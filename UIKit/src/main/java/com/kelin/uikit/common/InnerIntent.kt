package com.kelin.uikit.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.kelin.okpermission.OkActivityResult
import com.kelin.uikit.BaseFragmentActivity
import com.kelin.uikit.common.ImmersionMode.Companion.KEY_IMMERSION_MODE
import com.kelin.uikit.common.ImmersionMode.Companion.KEY_NAVIGATION_ICON
import com.kelin.uikit.flyweight.adapter.CommonFragmentStatePagerAdapter
import com.kelin.uikit.getString
import java.io.Serializable

/**
 * **描述:** 通用的自定义页面意图
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/10 5:30 PM
 *
 * **版本:** v 1.0.0
 */
class InnerIntent(context: Context, cls: Class<*>) : Intent(context, cls) {

    companion object {

        private const val KEY_INNER_INTENT_DEF_INDEX = "key_inner_intent_def_index"
        private const val KEY_INNER_INTENT_ADAPTER_CONFIG = "key_inner_intent_adapter_config"
        private const val KEY_INNER_INTENT_IS_FOR_TAB_MODE = "key_inner_intent_is_for_tab_mode"
        private const val KEY_INNER_INTENT_IS_FOR_TAB_MODE_SCROLL_ENABLE = "key_inner_intent_is_for_tab_mode_scroll_enable"

        /**
         * 判断是否为Tab模式的页面。
         */
        internal fun isForTab(intent: Intent): Boolean {
            return intent.getBooleanExtra(KEY_INNER_INTENT_IS_FOR_TAB_MODE, false)
        }

        /**
         * 判断ViewPage的滑动翻页是否可用。
         */
        internal fun isTabScrollEnable(intent: Intent): Boolean {
            return isForTab(intent) && intent.getBooleanExtra(KEY_INNER_INTENT_IS_FOR_TAB_MODE_SCROLL_ENABLE, false)
        }

        /**
         * 获取Tab模式下的默认页面索引。
         */
        internal fun getTabDefIndex(intent: Intent): Int {
            return intent.getIntExtra(KEY_INNER_INTENT_DEF_INDEX, 0)
        }

        /**
         * 获取Tab模式下Adapter配置。
         */
        internal fun getTabPageConfig(intent: Intent): PageConfigurationWrapper? {
            return intent.getSerializableExtra(KEY_INNER_INTENT_ADAPTER_CONFIG) as? PageConfigurationWrapper
        }
    }

    private var context: Context? = context

    var launchOptions: Bundle? = null
        internal set

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

    fun forTab(bool: Boolean, scrollEnable: Boolean) {
        putExtra(KEY_INNER_INTENT_IS_FOR_TAB_MODE, bool)
        putExtra(KEY_INNER_INTENT_IS_FOR_TAB_MODE_SCROLL_ENABLE, scrollEnable)
    }

    fun setTarget(target: Class<out Fragment>) {
        FragmentProvider.setTargetFragment(this, target)
    }

    /**
     * 为新的页面设置标题，仅在没有调用immersion或immersionToolbar方法时生效。
     * @param title 页面的标题。
     */
    fun title(title: CharSequence) {
        putExtra(BaseFragmentActivity.KEY_PAGE_TITLE, title)
    }

    /**
     * 为新的页面设置标题，仅在没有调用immersion或immersionToolbar方法时生效。
     * @param title 页面的标题。
     * @see immersion
     * @see immersionToolbar
     */
    fun title(@StringRes title: Int) {
        putExtra(BaseFragmentActivity.KEY_PAGE_TITLE, getString(title))
    }


    /**
     * 设置Tab模式下默认显示的页面索引。
     * @param index 默认显示的索引。
     */
    internal fun setDefIndex(index: Int) {
        putExtra(KEY_INNER_INTENT_DEF_INDEX, index)
    }

    /**
     * 设置Tab模式下的Adapter配置回调。
     */
    internal fun setTabAdapterConfig(configuration: CommonFragmentStatePagerAdapter.() -> Unit) {
        putExtra(KEY_INNER_INTENT_ADAPTER_CONFIG, PageConfigurationWrapper(configuration))
    }
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
 * 为新的页面设置沉浸式样式，设置沉浸式样式后在非Tab样式下会隐藏默认的Toolbar，如希望显示Toolbar请调用immersionToolbar方法设置沉浸式样式。
 * @see immersionToolbar
 */
fun Intent.immersion() {
    putExtra(KEY_IMMERSION_MODE, ImmersionMode.NO_TOOLBAR)
}

/**
 * 为新的页面设置沉浸式样式，设置沉浸式样式后Toolbar会盖在目标页面上，需要为目标页面设置一定的顶部间距保证Toolbar不会遮挡到目标页面的内容。
 * 如不想使用Toolbar则需要调用immersion方法设置沉浸式样式。
 * @see immersion
 */
fun Intent.immersionToolbar(@DrawableRes navigationIcon: Int? = null) {
    putExtra(KEY_IMMERSION_MODE, ImmersionMode.HAVE_TOOLBAR)
    putExtra(KEY_NAVIGATION_ICON, navigationIcon)
}

/**
 * Activity的启动配置。
 * @param options 配置内容。
 */
fun Intent.options(options: Bundle) {
    (this as InnerIntent).also { it.launchOptions = options }
}

/**
 * Activity的启动配置。
 * @param options 配置内容。
 */
fun Intent.options(options: Bundle.() -> Unit) {
    (this as InnerIntent).also {
        it.launchOptions = Bundle().apply { options(this) }
    }
}

/**
 * 设置默认页面索引。
 * @param index 默认的页面索引。
 */
fun Intent.defIndex(index: Int) {
    (this as InnerIntent).also { it.setDefIndex(index) }
}

/**
 * 配置页面。
 * @param configuration 页面的配置回调。
 */
fun Intent.configurePage(configuration: CommonFragmentStatePagerAdapter.() -> Unit) {
    (this as InnerIntent).also { it.setTabAdapterConfig(configuration) }
}

/**
 * 启动页面。
 * @param onResult 如果需要从目标页面获取结果则需要传入回调。
 */
fun Intent.start(onResult: ((resultCode: Int) -> Unit)?) {
    (this as? InnerIntent)?.also {
        if (onResult != null && it.isFromActivityContext) {
            OkActivityResult.startActivity(it.useActivity, this, it.launchOptions, onResult)
        } else {
            it.useContext.startActivity(this, launchOptions)
        }
    }
}

/**
 * 启动页面。
 * @param onResult 如果需要从目标页面获取结果则需要传入回调。
 */
fun <D> Intent.start(onResult: ((resultCode: Int, data: D?) -> Unit)?) {
    (this as? InnerIntent)?.also {
        if (onResult != null && it.isFromActivityContext) {
            OkActivityResult.startActivity(it.useActivity, this, it.launchOptions, onResult)
        } else {
            it.useContext.startActivity(this, launchOptions)
        }
    }
}

internal class PageConfigurationWrapper(val configuration: CommonFragmentStatePagerAdapter.() -> Unit) : Serializable