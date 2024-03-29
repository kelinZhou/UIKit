package com.kelin.uikit

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.os.PersistableBundle
import android.view.*
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.Fragment
import com.kelin.logger.Logger
import com.kelin.uikit.annotation.SoftInputModeFlags
import com.kelin.uikit.common.CommonErrorFragment
import com.kelin.uikit.core.AndroidBug5497Workaround
import com.kelin.uikit.core.SystemError
import com.kelin.uikit.tools.AppLayerErrorCatcher
import com.kelin.uikit.tools.BackHandlerHelper
import com.kelin.uikit.tools.ToastUtil
import com.kelin.uikit.tools.statusbar.StatusBarHelper
import java.util.*

/**
 * **描述:** 所有Activity的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-06 09:04
 *
 * **版本:** v 1.0.0
 */
abstract class BasicActivity : AppCompatActivity() {
    /**
     * 用来记录当前页面的Toolbar控件。
     */
    var toolbar: Toolbar? = null

    /**
     * 用来记录当前页面的标题控件。
     */
    private var tvTitle: TextView? = null

    /**
     * 用来记录当前页面的子标题控件。
     */
    private var tvSubtitle: TextView? = null

    var isActivityResumed = false
        private set

    protected open val hasFullScreen: Boolean = true

    protected var centerTitle = true

    private val statusBarHelper: StatusBarHelper by lazy {
        StatusBarHelper(
            this
        )
    }

    protected val toolbarPosition: IntArray
        get() {
            val location = IntArray(2)
            if (toolbar != null) {
                toolbar!!.getLocationInWindow(location)
                location[1] = location[1] + toolbar!!.height - StatusBarHelper.getStatusBarHeight(this)
            }
            return location
        }

    var useDefaultTransition: Boolean = true

    @get:IdRes
    protected open val leftButtonViewId: Int
        get() = R.id.toolbar_title_left_but

    val isActivityDestroyed: Boolean
        get() = (isDestroyed) || isFinishing

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return supportFragmentManager.fragments.any { if (it is BasicFragment) it.onKeyDown(keyCode, event) else false } || super.onKeyDown(keyCode, event)
    }

    fun setDarkMode(isDark: Boolean, refreshNavigationIcon: Boolean = false) {
        if (isDark) {
            StatusBarHelper.setStatusBarDarkMode(this)
        } else {
            StatusBarHelper.setStatusBarLightMode(this)
        }
        if (refreshNavigationIcon) {
            setNavigationIcon(if (isDark) R.drawable.ic_navigation_whit else R.drawable.ic_navigation_dark)
        }
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        super.startActivity(intent, options)
        overrideStartTransition()
    }

    @Deprecated("Deprecated in Java")
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        overrideStartTransition()
    }

    override fun finish() {
        super.finish()
        overrideExitTransition()
    }

    protected open fun overrideStartTransition() {
        if (useDefaultTransition) {
            overridePendingTransition(R.anim.anim_translate_x100_x0_300, R.anim.anim_translate_x0_x100minus_300)
        }
    }

    protected open fun overrideExitTransition() {
        if (useDefaultTransition) {
            overridePendingTransition(R.anim.anim_translate_x100minus_x0_300, R.anim.anim_translate_x0_x100_300)
        }
    }

    fun setNavigationIcon(@DrawableRes iconId: Int) {
        if (iconId != 0) {
            toolbar?.setNavigationIcon(iconId)
        } else {
            toolbar?.navigationIcon = null
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val fm = supportFragmentManager
        @SuppressLint("RestrictedApi") val frags = fm.fragments
        for (f in frags) {
            if (f is BasicFragment && event != null) {
                val isConsum = f.dispatchKeyEvent(event)
                return if (isConsum) isConsum else super.dispatchKeyEvent(event)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title ?: "")
    }

    fun setTitle(title: CharSequence?, center: Boolean) {
        centerTitle = center
        super.setTitle(title ?: "")
    }

    fun setSubTitle(title: CharSequence, center: Boolean) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            if (center) {
                if (tvSubtitle != null) {
                    tvSubtitle!!.visibility = View.VISIBLE
                    tvSubtitle!!.text = title
                }
            } else {
                actionBar.setDisplayShowTitleEnabled(true)
                actionBar.subtitle = title
            }
            if (centerTitle != center) {
                centerTitle = center
                setTitle(this.title, center)
            }
        }
    }

    protected open fun <V : View?> getView(@IdRes viewId: Int): V? {
        return findViewById<V>(viewId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //Android8.0如果Activity是透明的则会崩溃，但是小米手机上调用这行代码虽然会崩溃但仍然可以实现屏幕锁定。所以这里用try catch。
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } catch (ignore: Exception) {
            Logger.system("BasicActivity")?.i("=========锁定屏幕方向失败：${this.javaClass.simpleName}")
        }
    }

    final override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (hasFullScreen) {
            AndroidBug5497Workaround.assistActivity(this)
        }
        getView<View>(leftButtonViewId)?.setOnClickListener { onBackPressed() }
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        if (hasFullScreen) {
            AndroidBug5497Workaround.assistActivity(this)
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

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        if (hasFullScreen) {
            AndroidBug5497Workaround.assistActivity(this)
        }
    }

    protected fun disableHomeAsUp() {
        setNavigationIcon(0)
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        if (centerTitle) {
            tvTitle?.text = title
//            tvTitle?.setTextColor(color)
        } else {
            toolbar?.title = title
//            toolbar?.setTitleTextColor(color)
        }
    }


    fun setSubTitle(title: CharSequence) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            if (centerTitle) {
                if (tvSubtitle != null) {
                    tvSubtitle!!.visibility = View.VISIBLE
                    tvSubtitle!!.text = title
                }
            } else {
                actionBar.setDisplayShowTitleEnabled(true)
                actionBar.subtitle = title
            }
        }
    }

    /**
     * 覆盖软键盘模式。调用改方法前必须确保[.overrideWindowSoftInputModeEnable]返回true，否则该方法不会生效。
     *
     * @param mode 要设置的模式。
     * @see .overrideWindowSoftInputModeEnable
     */
    fun overrideWindowSoftInputMode(@SoftInputModeFlags mode: Int) {
        if (overrideWindowSoftInputModeEnable()) {
            window.setSoftInputMode(mode)
        }
    }

    /**
     * 是否可以覆盖软键盘的弹出模式。
     *
     * 如果当前的Activity中包含了[BasicFragment]实例，
     * 并且覆盖了[BasicFragment.windowSoftInputModeFlags]方法或者调用了 [ ][BasicFragment.overrideWindowSoftInputMode]方法的话会导致[BaseActivity]本身所配置的软键盘启动模式失效，
     * 无论是通过清单文件配置还是代码配置，在[BasicFragment]启动后都会失效，如果你希望在这种情况下以[BaseActivity]为准，
     * 则需要覆盖该方法并返回false。如果返回了false，则[.overrideWindowSoftInputMode]方法就会失效。
     *
     * @return 如果允许覆盖则返回true，否则返回false。默认实现为可以覆盖。
     * @see .overrideWindowSoftInputMode
     * @see BasicFragment.overrideWindowSoftInputMode
     * @see BasicFragment.windowSoftInputModeFlags
     */
    open fun overrideWindowSoftInputModeEnable(): Boolean {
        return true
    }


    fun initTitleBar(
        toolbar: Toolbar?,
        titleView: TextView?,
        subtitleView: TextView?,
        navigationIcon: Int = R.drawable.ic_navigation_dark
    ) {
        if (toolbar != null) {
            this.toolbar = toolbar
            this.tvTitle = titleView
            this.tvSubtitle = subtitleView
            setSupportActionBar(toolbar)

            titleView?.compoundDrawablePadding = 5f.dp2px
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false)
                actionBar.setDisplayHomeAsUpEnabled(false)
            }
            if (navigationIcon != 0) {
                setNavigationIcon(navigationIcon)
            }
            val parent = toolbar.parent as? View
            if (parent != null) {
                setActionBarView(parent)
            }
//            toolbar.setTitleTextAppearance(this, R.style.common_title_text_style)
        }
    }

    protected fun setActionBarView(view: View?) {
        statusBarHelper.setActionBarView(view)
    }

    protected fun onToolBarLeftClick() {
        finish()
    }

    fun processStatusBar(@ColorInt color: Int, isDark: Boolean? = null) {
        statusBarHelper.process(color)
        if (isDark != null) {
            if (isDark) {  //这里加这语句是因为如果onStart中的代码执行之后又执行了这里会导致设置失效。
                StatusBarHelper.setStatusBarDarkMode(this)
            } else {
                StatusBarHelper.setStatusBarLightMode(this)
            }
        }
    }

    fun processToolbar(@ColorInt color: Int) {
        (toolbar?.parent as? View)?.setBackgroundColor(color)
    }

    /**
     * 获取当需要跳转到错误页面时的错误页面实例。
     */
    protected open fun onJumpError(systemError: SystemError, exception: Throwable = RuntimeException(systemError.text)): CommonErrorFragment {
        ToastUtil.showShortToast(systemError.text)
        AppLayerErrorCatcher.throwException(exception)
        return CommonErrorFragment.createInstance(systemError)
    }

    @JvmOverloads
    protected fun addFragment(containerViewId: Int, fragment: Fragment, tag: String = fragment.javaClass.simpleName) {
        this.supportFragmentManager
            .beginTransaction()
            .add(containerViewId, fragment, tag)
            .commit()
    }


    protected fun replaceFragment(containerViewId: Int, fragment: Fragment) {
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(containerViewId, fragment, fragment.javaClass.simpleName)
        fragmentTransaction.setCustomAnimations(
            R.anim.anim_translate_x100_x0_300,
            R.anim.anim_translate_x0_x100minus_300,
            R.anim.anim_translate_x100minus_x0_300,
            R.anim.anim_translate_x0_x100_300
        )
        fragmentTransaction.commitAllowingStateLoss()
    }


    @JvmOverloads
    protected fun pushFragment(
        containerViewId: Int,
        fragment: Fragment,
        tag: String = fragment.javaClass.simpleName
    ) {
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(containerViewId, fragment, tag).addToBackStack(null)
        fragmentTransaction.commitAllowingStateLoss()
    }

    public override fun onResume() {
        super.onResume()
        isActivityResumed = true
    }

    public override fun onPause() {
        isActivityResumed = false
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        Logger.system("BasicActivity")?.i("onSupportNavigateUp")
        return super.onSupportNavigateUp()
    }

    override fun onCreateSupportNavigateUpTaskStack(builder: TaskStackBuilder) {
        super.onCreateSupportNavigateUpTaskStack(builder)
        Logger.system("BasicActivity")?.i("onCreateSupportNavigateUpTaskStack")
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        Logger.system("BasicActivity")?.i("onPrepareOptionsMenu")
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onOptionsMenuClosed(menu: Menu) {
        Logger.system("BasicActivity")?.i("onOptionsMenuClosed")
        super.onOptionsMenuClosed(menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        Logger.system("BasicActivity")?.i("onMenuOpened")
        return super.onMenuOpened(featureId, menu)
    }

    /**
     * 解决系统字体变大导致界面错乱的问题。
     */
    override fun getResources(): Resources {
        val res = super.getResources()
        val config = res?.configuration
        if (res != null && config != null && config.fontScale != 1f) {
            config.fontScale = 1f
            res.updateConfiguration(config, res.displayMetrics)
        }
        return res
    }

    override fun onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed()
        }
    }
}
