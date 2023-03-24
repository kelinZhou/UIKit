package com.kelin.uikit.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.kelin.okpermission.OkActivityResult

/**
 * **描述:** 页面启动的配置项。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/21 1:40 PM
 *
 * **版本:** v 1.0.0
 */
abstract class AbsOption(context: Context) {
    val intent = Intent(context, Navigation::class.java)
    private var mLaunchOptions: Bundle? = null
    private var onResultInfo: OnResultInfo<*>? = null
    val launchOptions: Bundle?
        get() = mLaunchOptions

    private var context: Context? = context

    internal val isFromActivityContext: Boolean
        get() = context is Activity

    internal val useActivity: Activity
        get() = useContext as Activity

    internal val useContext: Context
        get() {
            val contextTemp = context
            context = null
            return contextTemp ?: throw NullPointerException("The context used early!")
        }

    fun setPageMode(mode: PageMode) {
        intent.putExtra(Option.KEY_PAGE_MODE, mode)
    }

    open fun setTarget(target: Class<out Fragment>) {
        intent.putExtra(Option.KEY_TARGET_PAGE, target)
    }

    /**
     * Activity的启动配置。
     * @param options 配置内容。
     */
    fun options(options: Bundle?) {
        mLaunchOptions = options
    }

    /**
     * Activity的启动配置。
     * @param options 配置内容。
     */
    fun options(options: Bundle.() -> Unit) {
        mLaunchOptions = Bundle().apply { options(this) }
    }

    /**
     * 设置返回结果回调。
     * @param callback 回调函数。
     */
    fun<D> results(callback: ((data: D?) -> Unit)?) {
        onResultInfo = callback?.let { OnResultInfo.create1(it) }
    }

    /**
     * 设置返回结果回调。
     * @param callback 回调函数。
     */
    fun resultsForCode(callback: ((resultCode: Int) -> Unit)?) {
        onResultInfo = callback?.let { OnResultInfo.create2(it) }
    }

    fun start() {
        if (onResultInfo.isNullOrEmpty || !isFromActivityContext) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            useContext.startActivity(intent, launchOptions)
        } else {
            when {
                onResultInfo!!.onResult1 != null -> {
                    OkActivityResult.startActivity(useActivity, intent, launchOptions, onResultInfo!!.onResult1!!)
                }
                onResultInfo!!.onResult2 != null -> {
                    OkActivityResult.startActivityForCode(useActivity, intent, launchOptions, onResultInfo!!.onResult2!!)
                }
            }
        }
    }
}

private class OnResultInfo<D> private constructor(
    val onResult1: ((data: D?) -> Unit)?,
    val onResult2: ((resultCode: Int) -> Unit)?,
){

    companion object {
        fun <D> create1(callback: (data: D?) -> Unit): OnResultInfo<D> {
            return OnResultInfo(callback, null)
        }
        fun create2(callback: (resultCode: Int) -> Unit): OnResultInfo<Any> {
            return OnResultInfo(null, callback)
        }
    }

    init {
        if (isNullOrEmpty) {
            throw NullPointerException("The callback must not be null !")
        }
    }
}

private val OnResultInfo<*>?.isNullOrEmpty: Boolean
    get() = this == null || (onResult1 == null && onResult2 == null)

enum class PageMode {
    /**
     * 普通的。
     */
    NORMAL,

    /**
     * TabLayout+ViewPager。
     */
    TAB,

    /**
     * 搜索。
     */
    SEARCH
}