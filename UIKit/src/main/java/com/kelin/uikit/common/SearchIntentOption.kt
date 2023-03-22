package com.kelin.uikit.common

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * **描述:** Tab页面配置的具体实现。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/21 11:39 AM
 *
 * **版本:** v 1.0.0
 */
class SearchIntentOption(context: Context) : AbsOption(context), SearchOption{
    override fun setTarget(target: Class<out Fragment>) {
        super.setTarget(target)
        setPageMode(PageMode.SEARCH)
    }
}