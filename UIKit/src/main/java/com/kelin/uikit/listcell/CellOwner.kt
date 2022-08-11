package com.kelin.uikit.listcell

import android.view.View

/**
 * **描述:** Cell的拥有者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/10/20 1:26 PM
 *
 * **版本:** v 1.0.0
 */
interface CellOwner {
    val cellItemView: View

    val cellLayoutPosition: Int

    fun setIsRecyclable(recyclable: Boolean)

    fun refreshItemBackground()

    fun layoutItemData(): Any
}