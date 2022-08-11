package com.kelin.uikit.tools.statistics

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

/**
 * **描述:** 统计处理者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2022/8/11 5:01 PM
 *
 * **版本:** v 1.0.0
 */
interface StatisticsHandler {

    /**
     * 预初始化。
     */
    fun preInit(handler: StatisticsHandler)

    /**
     * 统计的初始化，如果不是在Application的onCreate中调用的就必须在Application的onCreate中先调用```StatisticsHelper.preInit(this)```方法。
     */
    fun init()

    /**
     * 统计用户登录。
     */
    fun onProfileSignIn(context: Context, userId: Long, type: SignInType)

    /**
     * 统计用户退出登录。
     */
    fun onProfileSignOut(context: Context)

    fun onActivityStart(activity: Activity)

    fun onActivityEnd(activity: Activity)

    fun onActivityResume(activity: Activity)

    fun onActivityPause(activity: Activity)

    /**
     * 统计页面启动。
     */
    fun onPageResume(fragment: Fragment, pageName: String = fragment.javaClass.simpleName)

    /**
     * 统计页面结束。
     */
    fun onPagePause(fragment: Fragment, pageName: String = fragment.javaClass.simpleName)
}