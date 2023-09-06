package com.kelin.uikit.common

/**
 * **描述:** 状态栏模式。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/9/6 10:11 AM
 *
 * **版本:** v 1.0.0
 */
sealed class StatusBarMode(val code: Int) {
    companion object {
        val values by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { listOf(Light, Dark, Gone) }
    }

    /**
     * 白底黑字。
     */
    object Light : StatusBarMode(1)

    /**
     * 黑底白字。
     */
    object Dark : StatusBarMode(2)

    /**
     * 隐藏状态栏，不显示文字。
     */
    object Gone : StatusBarMode(3)
}
