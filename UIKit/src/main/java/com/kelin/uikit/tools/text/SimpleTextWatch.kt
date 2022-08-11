package com.kelin.uikit.tools.text

import android.text.Editable
import android.widget.TextView
import java.lang.ref.WeakReference

/**
 * **描述:** 具有页面状态的View
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-9  8:42
 *
 * **版本:** v 1.0.0
 */
abstract class SimpleTextWatch protected constructor(textView: TextView) : TextWatchImpl() {
    private val mView: WeakReference<TextView> = WeakReference(textView)
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        val et = mView.get()
        et?.let { beforeTextChanged(it, s, start, count, after) }
    }

    override fun afterTextChanged(s: Editable) {
        val et = mView.get()
        et?.let { afterTextChanged(it, s) }
    }

    protected open fun beforeTextChanged(et: TextView, s: CharSequence, start: Int, count: Int, after: Int) {}

    protected abstract fun afterTextChanged(et: TextView, s: Editable)
}