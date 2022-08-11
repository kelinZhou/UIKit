package com.kelin.uikit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.kelin.uikit.core.SystemError
import com.kelin.uikit.tools.AppLayerErrorCatcher
import com.kelin.uikit.tools.ToastUtil
import com.kelin.uikit.tools.statusbar.StatusBarHelper
import com.kelin.uikit.common.CommonErrorFragment

/**
 * **描述:** 用来承载Fragment的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-09-26  16:23
 *
 * **版本:** v 1.0.0
 */
abstract class BaseFragmentActivity : BasicActivity() {

    @get:LayoutRes
    protected open val activityRootLayout: Int
        get() = R.layout.activity_common_layout

    @get:IdRes
    protected open val warpFragmentId: Int
        get() = R.id.fragment_container

    @get:IdRes
    protected open val toolbarId: Int
        get() = R.id.my_awesome_toolbar

    @get:IdRes
    protected open val toolbarTitleViewId: Int
        get() = R.id.toolbar_center_title

    @get:IdRes
    protected open val toolbarSubTitleViewId: Int
        get() = R.id.toolbar_sub_title

    @get:IdRes
    protected open val leftButtonViewId: Int
        get() = R.id.toolbar_title_left_but

    protected val curTarget: Int
        get() = intent.getIntExtra(KEY_TARGET_PAGE, PAGE_UNKNOWN)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        super.onCreate(savedInstanceState)
        setContentView(activityRootLayout)
        title = intent.getCharSequenceExtra(KEY_PAGE_TITLE)
        initTitleBar(getView(toolbarId), getView(toolbarTitleViewId), getView(toolbarSubTitleViewId))
        addInitialFragment().also {
            (it as? BasicFragment)?.isDarkMode?.also { isDark ->
                if (isDark) {
                    StatusBarHelper.setStatusBarDarkMode(this)
                } else {
                    StatusBarHelper.setStatusBarLightMode(this)
                }
            }
        }
        getView<View>(leftButtonViewId)?.setOnClickListener { onBackPressed() }
    }

    /**
     * 显示文字导航按钮。
     */
    protected fun showTextNavigation(@StringRes btnText: Int) {
        disableHomeAsUp()
        getView<TextView>(leftButtonViewId)?.run {
            visibility = View.VISIBLE
            text = getString(btnText)
        }
    }

    /**
     * 显示文字导航按钮。
     */
    protected fun showTextNavigation(btnText: CharSequence) {
        disableHomeAsUp()
        getView<TextView>(leftButtonViewId)?.run {
            visibility = View.VISIBLE
            text = btnText
        }
    }

    protected fun hideTextNavigation(@DrawableRes iconId: Int? = null) {
        getView<TextView>(leftButtonViewId)?.visibility = View.GONE
        if (iconId != null) {
            setNavigationIcon(iconId)
        }
    }

    fun hideToolbarLine() {
        getView<View>(R.id.vToolbarLine)?.visibility = View.GONE
    }

    private fun addInitialFragment(): Fragment {
        val intent = intent
        return if (curTarget == PAGE_UNKNOWN) {
            if (UIKit.isDebugMode) {
                throw RuntimeException("The target page value is unknown!")
            } else {
                val fragment = onJumpError(SystemError.NULL_ARGUMENT, RuntimeException("The target page value is unknown!"))
                replaceFragment(warpFragmentId, fragment)
                fragment
            }
        } else {
            val fragment = getCurrentFragment(curTarget, intent)
            replaceFragment(warpFragmentId, fragment)
            fragment
        }
    }

    protected open fun onJumpError(systemError: SystemError, exception: Throwable = RuntimeException(systemError.text)): CommonErrorFragment {
        ToastUtil.showShortToast(systemError.text)
        AppLayerErrorCatcher.throwException(exception)
        return CommonErrorFragment.createInstance(systemError)
    }

    protected abstract fun getCurrentFragment(targetPage: Int, intent: Intent): Fragment

    companion object {

        /**
         * 用来获取目标页面的键。
         */
        private const val KEY_TARGET_PAGE = "key_target_page"
        private const val KEY_PAGE_TITLE = "key_page_title"

        /**
         * 表示当前目标页面为未知的。
         */
        private const val PAGE_UNKNOWN = 0

        fun getJumpIntent(context: Context, activityClass: Class<out BaseFragmentActivity>, targetPage: Int, pageTitle: CharSequence = ""): Intent {
            val intent = generateJumpIntent(context, activityClass)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra(KEY_TARGET_PAGE, targetPage)
            intent.putExtra(KEY_PAGE_TITLE, pageTitle)
            return intent
        }
    }
}