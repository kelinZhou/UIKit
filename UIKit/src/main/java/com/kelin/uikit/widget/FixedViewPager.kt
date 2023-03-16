package com.kelin.uikit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * **描述:** 修复ViewPager的先天不足。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/16 10:56 AM
 *
 * **版本:** v 1.0.0
 */
class FixedViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

    var isUserInputEnable: Boolean = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isUserInputEnable && super.onTouchEvent(ev)
    }
}