package com.kelin.uikit.tools.statistics

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

/**
 * **描述:** 统计的帮助类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-05  18:06
 *
 * **版本:** v 1.0.0
 */
object StatisticsHelper : StatisticsHandler {

    private var mHandler: StatisticsHandler? = null

    private var hasInit = false

    private val canStatistics: Boolean
        get() = hasInit

    /**
     * 预初始化。
     */
    override fun preInit(handler: StatisticsHandler) {
        mHandler = handler
    }

    /**
     * 统计的初始化，如果不是在Application的onCreate中调用的就必须在Application的onCreate中先调用```StatisticsHelper.preInit(this)```方法。
     */
    override fun init() {
        if (!hasInit) {
            hasInit = true
            mHandler?.init()
        }
    }

    /**
     * 统计用户登录。
     */
    override fun onProfileSignIn(context: Context, userId: Long, type: SignInType) {
        if (canStatistics) {
            mHandler?.onProfileSignIn(context, userId, type)
        }
    }

    /**
     * 统计用户退出登录。
     */
    override fun onProfileSignOut(context: Context) {
        if (canStatistics) {
            mHandler?.onProfileSignOut(context)
        }
    }

    override fun onActivityStart(activity: Activity) {
        if (canStatistics) {
            mHandler?.onActivityStart(activity)
        }
    }

    override fun onActivityEnd(activity: Activity) {
        if (canStatistics) {
            mHandler?.onActivityEnd(activity)
        }
    }

    override fun onActivityResume(activity: Activity) {
        if (canStatistics) {
            mHandler?.onActivityResume(activity)
        }
    }

    override fun onActivityPause(activity: Activity) {
        if (canStatistics) {
            mHandler?.onActivityPause(activity)
        }
    }

    /**
     * 统计页面启动。
     */
    override fun onPageResume(fragment: Fragment, pageName: String) {
        if (canStatistics) {
            mHandler?.onPageResume(fragment, pageName)
        }
    }

    /**
     * 统计页面结束。
     */
    override fun onPagePause(fragment: Fragment, pageName: String) {
        if (canStatistics) {
            mHandler?.onPagePause(fragment, pageName)
        }
    }
}