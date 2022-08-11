package com.kelin.uikit.listcell

import android.view.View
import androidx.annotation.CallSuper
import com.kelin.uikit.dp2px

/**
 * **描述:** 列表中可以根据位置改变padding的Cell。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/4/24 9:41 PM
 *
 * **版本:** v 1.0.0
 */
abstract class MutablePaddingCell(isLast: Boolean, isFirst: Boolean = false, private val defaultPadding: Float = 0F) : MutableCell(isLast, isFirst) {

    private val defPadding by lazy { defaultPadding.dp2px }
    private val specialPadding by lazy { 20.dp2px }

    @CallSuper
    override fun onBindData(iv: View) {
        iv.setPadding(iv.paddingLeft, if (isFirst) specialPadding else defPadding, iv.paddingRight, if (isLast) specialPadding else defPadding)
    }
}