package com.kelin.uikit.widget.tablayout

import android.content.Context
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 * **描述:** TabLayout的Tab。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/8/16 7:15 PM
 *
 * **版本:** v 1.0.0
 */
class CommonToolbarTabTitleView @JvmOverloads constructor(context: Context?, private val listener: OnSelectedListener? = null) : SimplePagerTitleView(context) {
    override fun onSelected(index: Int, totalCount: Int) {
        super.onSelected(index, totalCount)
        isSelected = true
        paint.isFakeBoldText = true
        textSize = 18F
        setTextColor(mSelectedColor)
        if (mSelectedColor == mNormalColor) {
            invalidate()
        }
        listener?.onSelected(index)
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        super.onDeselected(index, totalCount)
        isSelected = false
        textSize = 16F
        paint.isFakeBoldText = false
        setTextColor(mNormalColor)
        if (mSelectedColor == mNormalColor) {
            invalidate()
        }
    }
}