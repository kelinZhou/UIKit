package com.kelin.uikit.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.kelin.uikit.R

/**
 * **描述:** 可以限定最大尺寸的相对布局。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/8/16 7:15 PM
 *
 * **版本:** v 1.0.0
 */
@SuppressLint("CustomViewStyleable")
class MaxSizeRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr), MaxSizeView {
    private var maxHeight = 0
    private var maxWidth = 0

    init {
        MaxSizeView.parserStyleable(context.obtainStyledAttributes(attrs, R.styleable.MaxSizeView), this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        MaxSizeView.measureSize(maxWidth, maxHeight, widthMeasureSpec, heightMeasureSpec, this)
    }

    @SuppressLint("WrongCall")
    override fun onRealMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun setMaxWidth(maxWidth: Int) {
        this.maxWidth = maxWidth
        requestLayout()
    }

    override fun setMaxHeight(maxHeight: Int) {
        this.maxHeight = maxHeight
    }
}