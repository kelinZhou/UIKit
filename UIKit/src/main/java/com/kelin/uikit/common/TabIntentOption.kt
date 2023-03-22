package com.kelin.uikit.common

import android.content.Context
import com.kelin.uikit.common.TabOption.Companion.KEY_INNER_INTENT_IS_FOR_TAB_MODE_SLIDE_ENABLE

/**
 * **描述:** Tab页面配置的具体实现。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/21 11:39 AM
 *
 * **版本:** v 1.0.0
 */
class TabIntentOption(context: Context) : AbsOption(context), TabOption {
    fun setSlideEnable(slideEnable: Boolean) {
        intent.putExtra(KEY_INNER_INTENT_IS_FOR_TAB_MODE_SLIDE_ENABLE, slideEnable)
        setPageMode(PageMode.TAB)
    }
}