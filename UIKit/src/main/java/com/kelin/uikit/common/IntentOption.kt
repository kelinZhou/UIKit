package com.kelin.uikit.common

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.kelin.uikit.common.IOption.Companion.KEY_PAGE_TITLE
import com.kelin.uikit.common.IOption.Companion.KEY_PAGE_TITLE_CENTER
import com.kelin.uikit.common.IOption.Companion.KEY_PAGE_TITLE_COLOR
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
class IntentOption(context: Context) : AbsOption(context), Option {

    override fun setTarget(target: Class<out Fragment>) {
        super.setTarget(target)
        setPageMode(PageMode.NORMAL)
    }

    /**
     * 为新的页面设置标题，仅在没有调用immersion或immersionToolbar方法时生效。
     * @param title 页面的标题。
     * @param color 页面标题的颜色。
     * @see immersion
     * @see immersionToolbar
     */
    fun title(title: CharSequence, @ColorInt color: Int?) {
        intent.putExtra(KEY_PAGE_TITLE, title)
        intent.putExtra(KEY_PAGE_TITLE_COLOR, color)
    }

    /**
     * 为新的页面设置标题，仅在没有调用immersion或immersionToolbar方法时生效。
     * @param title 页面的标题。
     * @param color 页面标题的颜色。
     * @see immersion
     * @see immersionToolbar
     */
    fun title(@StringRes title: Int, @ColorInt color: Int?) {
        intent.putExtra(KEY_PAGE_TITLE, getString(title))
        intent.putExtra(KEY_PAGE_TITLE_COLOR, color)
    }

    /**
     * 为新的页面设置标题是否居中显示，仅在没有调用immersion或immersionToolbar方法时生效。
     * @param center 标题是否居中显示。
     * @see immersion
     * @see immersionToolbar
     */
    override fun titleCenter(center: Boolean) {
        intent.putExtra(KEY_PAGE_TITLE_CENTER, center)
    }
}

internal class PageConfigurationWrapper(val optional: CommonFragmentStatePagerAdapter.() -> Unit) : Serializable