package com.kelin.uikit.widget.draggablelayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * **描述:** 可拖拽布局中可以使用的ScrollView。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-09-01 15:22:12
 *
 * **版本:** v 1.0.0
 */
class DraggableScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ScrollView(context, attrs, defStyle) {

    private var listener: ((sv: DraggableScrollView, l: Int, t: Int, oldL: Int, oldT: Int) -> Unit)? = null

    fun setScrollChangeListener(listener: (sv: DraggableScrollView, l: Int, t: Int, oldL: Int, oldT: Int) -> Unit) {
        this.listener = listener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        listener?.invoke(this, l, t, oldl, oldt)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val parent = this.parent
        if (parent is DraggableLayout) {
            if (parent.currentStatus == DraggableLayout.STATUS_MIDDLE)
                return false
        }
        return super.onTouchEvent(ev)
    }
}