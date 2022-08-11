package com.kelin.uikit.tools

import android.util.SparseArray
import android.view.View

/**
 * **描述:** 双击的处理者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/9/13 2:13 PM
 *
 * **版本:** v 1.0.0
 */
class DoubleClickHandler(private val timeSpace: Long = 500) {
    private val clickPool = SparseArray<Long>()

    fun isNotFastClick(id: Int, duration: Long? = timeSpace): Boolean {
        return !isDoubleClick(id, duration)
    }

    fun isNotFastClick(view: View, duration: Long? = timeSpace): Boolean {
        return !isDoubleClick(view, duration)
    }

    fun isDoubleClick(view: View, duration: Long? = timeSpace): Boolean {
        return isDoubleClick(view.id, duration)
    }

    fun isDoubleClick(id: Int, duration: Long? = timeSpace): Boolean {
        val last = clickPool[id]
        val cur = System.currentTimeMillis()
        return (last != null && cur - last <= duration ?: timeSpace).also {
            clickPool.put(id, cur)
        }
    }
}