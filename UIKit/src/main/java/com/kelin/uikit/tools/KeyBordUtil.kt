package com.kelin.uikit.tools

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager

/**
 * **描述:** 软键盘工具。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/3/26  8:54 PM
 *
 * **版本:** v 1.0.0
 */
object KeyBordUtil {
    /**
     * 显示和隐藏软键盘 View ： EditText、TextView isShow : true = show , false = hide
     */
    fun toggleSoftKeyboard(view: View) {
        if (isSoftInputShown(view.context as Activity)) {
            hideSoftKeyboard(view)
        } else {
            showSoftKeyboard(view)
        }
    }

    /**
     * 强制隐藏软键盘。
     *
     * @param view 当前触摸的View。
     */
    fun hideSoftKeyboard(view: View) {
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 强制弹出软键盘。
     *
     * @param view 要显示的View。
     */
    fun showSoftKeyboard(view: View) {
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    /**
     * 判断软键盘是否已经被显示。
     */
    fun isSoftKeyboardShowing(window: Window): Boolean {
        //获取当前屏幕内容的高度
        val screenHeight = window.decorView.height
        //获取View可见区域的bottom
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        return screenHeight - rect.bottom != 0
    }

    /**
     * 获取软件盘的高度
     */
    fun getSupportSoftInputHeight(activity: Activity): Int {
        val r = Rect()
        /*
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */activity.window.decorView.getWindowVisibleDisplayFrame(r)
        //获取屏幕的高度
        val screenHeight = activity.window.decorView.rootView.height
        //计算软件盘的高度
        var softInputHeight = screenHeight - r.bottom
        /*
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight -= getSoftButtonsBarHeight(activity)
        }
        return softInputHeight
    }

    /**
     * 底部虚拟按键栏的高度
     */
    fun getSoftButtonsBarHeight(activity: Activity): Int {
        val metrics = DisplayMetrics()
        //这个方法获取可能不是真实屏幕的高度
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        //获取当前屏幕的真实高度
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }

    /**
     * 是否显示软件盘
     */
    fun isSoftInputShown(activity: Activity): Boolean {
        return getSupportSoftInputHeight(activity) != 0
    }

    fun updateSoftInputMethod(activity: Activity, softInputMode: Int) {
        if (!activity.isFinishing) {
            val params = activity.window.attributes
            if (params.softInputMode != softInputMode) {
                params.softInputMode = softInputMode
                activity.window.attributes = params
            }
        }
    }
}