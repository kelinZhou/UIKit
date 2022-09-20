package com.kelin.uikit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kelin.logger.Logger
import com.kelin.uikit.annotation.SoftInputModeFlags
import com.kelin.uikit.tools.statistics.StatisticsHelper


/**
 * **描述:** Fragment的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/3/27  6:08 PM
 *
 * **版本:** v 1.0.0
 */
abstract class BasicFragment : Fragment() {

    private val handler by lazy { Handler() }

    open val isDarkMode: Boolean?
        get() = false

    private var hasStatisticsStart = false

    protected open val needStatistics: Boolean
        get() = true

    protected val isRealResumed: Boolean
        @SuppressLint("RestrictedApi")
        get() {
            return isResumed && (userVisibleHint || isMenuVisible)
        }

    protected open val statisticsName: String
        get() = javaClass.simpleName

    /**
     * 获取当前[BasicFragment]的软键盘启动模式。
     *
     * @return 返回当前 [BasicFragment] 的你所期望的软键盘启动模式。过个启动模式可以使用 "|" (或运算符)连接。
     * @see .overrideWindowSoftInputMode
     */
    @get:SoftInputModeFlags
    protected open val windowSoftInputModeFlags: Int = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED //默认实现，返回未指定任何模式的flag。

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    protected fun setUseDefaultTransition(use: Boolean) {
        (activity as? BasicActivity)?.useDefaultTransition = use
    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
        // FragmentStatePagerAdapter保存状态有问题，可能导致getUserVisibleHint为false，所以加上isMenuVisible一起判断
        if (userVisibleHint || isMenuVisible) {
            onRealResume()
        }
        if (needStatistics && parentFragment == null) {
            hasStatisticsStart = true
            Logger.system("BasicFragment")?.i("===============pageStart:${statisticsName}")
            StatisticsHelper.onPageResume(this, statisticsName)
        }
    }

    fun hideToolbarLine() {
        (activity as? BaseFragmentActivity)?.hideToolbarLine()
    }

    fun setNavigationIcon(@DrawableRes drawable: Int) {
        (requireActivity() as? BasicActivity)?.setNavigationIcon(drawable)
    }

    open fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }

    protected fun postDelayed(delayMillis: Long, runnable: () -> Unit) {
        handler.postDelayed(runnable, delayMillis)
    }

    override fun onPause() {
        if (userVisibleHint) {
            onRealPause()
        }
        super.onPause()
        if (hasStatisticsStart) {
            Logger.system("BasicFragment")?.i("===============pageStop:${statisticsName}")
            StatisticsHelper.onPagePause(this, statisticsName)
            hasStatisticsStart = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isResumed) {
            if (isVisibleToUser) {
                onRealResume()
            } else {
                onRealPause()
            }
        }
        Logger.system("BasicFragment")?.i("setUserVisibleHint: isVisibleToUser? $isVisibleToUser")
    }

    @CallSuper
    protected open fun onRealResume() {
    }

    protected open fun onRealPause() {
    }

    protected fun hideToolbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        hideToolbarLine()
    }

    protected fun showToolbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    protected fun processStatusBar(@ColorInt color: Int) {
        (activity as? BasicActivity)?.processStatusBar(color)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BasicActivity) {
            overrideWindowSoftInputMode(windowSoftInputModeFlags)
        }
        Logger.system("BasicFragment")?.i("onAttachView")
    }

    /**
     * 覆盖软键盘弹出模式。该方法的实现方式为调用{@link BasicActivity#overrideWindowSoftInputMode(int)}，所以
     * 如果想要该方法生效必须保证当前{@link BasicFragment}是依附于{@link BasicActivity}的。
     * 另外还要确保{@link BasicActivity#overrideWindowSoftInputModeEnable()}方法的返回值为true。
     * <p>通常情况下该方法是不需要手动调用的，也不能被覆盖。该方法会在当前{@link BasicFragment}与{@link BasicActivity}
     * 发生关联并且是最初关联的时候调用一次，具体的参数值是通过{@link #windowSoftInputModeFlags()}方法返回的，
     * 也就是说一般情况下你只需要覆盖{@link #windowSoftInputModeFlags()}方法就可以了。
     * <p>调用该方法前你必须确保{@link #getActivity()}方法的返回值不为null，否则会抛出{@link NullPointerException}。
     *
     * @param flags 要设置的模式。
     * @see BasicActivity.overrideWindowSoftInputModeEnable()
     * @see BasicActivity.overrideWindowSoftInputMode(int)
     * @see windowSoftInputModeFlags()
     */
    fun overrideWindowSoftInputMode(@SoftInputModeFlags flags: Int) {
        if (activity != null) {
            if (activity is BasicActivity) {
                (activity as BasicActivity).overrideWindowSoftInputMode(flags)
            }
        } else {
            throw NullPointerException("The activity must not null!")
        }
    }

    protected fun finishActivity() {
        activity?.finish()
    }

    open fun onInterceptBackPressed(): Boolean {
        return false
    }

    protected fun setTitle(@StringRes title: Int) {
        setTitle(context.getString(title))
    }

    protected fun setTitle(title: CharSequence?) {
        (activity as? BasicActivity)?.title = title
    }

    protected fun getTitle(): CharSequence? {
        return (activity as? BasicActivity)?.title
    }

    override fun getContext(): Context {
        return super.getContext() ?: activity ?: UIKit.getContext()
    }

    @ColorInt
    protected fun getColor(@ColorRes color: Int): Int {
        return resources.getColor(color)
    }

    companion object {
        fun newInstance(clazz: Class<out Fragment>, args: Bundle?): Fragment {
            try {
                val f = clazz.newInstance()
                if (args != null) {
                    args.classLoader = f.javaClass.classLoader
                    f.arguments = args
                }
                return f
            } catch (e: Exception) {
                try {
                    val constructor = clazz.getDeclaredConstructor()
                    constructor.isAccessible = true
                    val f = constructor.newInstance() as Fragment
                    if (args != null) {
                        args.classLoader = f.javaClass.classLoader
                        f.arguments = args
                    }
                    return f
                } catch (e: Exception) {
                    throw InstantiationException("Unable to instantiate fragment $clazz: make sure class warehouseName exists, is public, and has an empty constructor that is public", e)
                }
            }

        }
    }
}