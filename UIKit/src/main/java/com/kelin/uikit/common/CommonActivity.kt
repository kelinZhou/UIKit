package com.kelin.uikit.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.kelin.uikit.*
import com.kelin.uikit.common.ImmersionMode.Companion.KEY_IMMERSION_MODE
import com.kelin.uikit.common.ImmersionMode.Companion.KEY_NAVIGATION_ICON
import com.kelin.uikit.core.SystemError
import com.kelin.uikit.flyweight.adapter.CommonFragmentStatePagerAdapter
import com.kelin.uikit.tools.DeviceUtil
import com.kelin.uikit.tools.statusbar.StatusBarHelper
import com.kelin.uikit.widget.FixedViewPager
import com.kelin.uikit.widget.MaxSizeRelativeLayout
import com.kelin.uikit.widget.tablayout.helper.ViewPagerTabLayoutHelper

/**
 * **描述:** 通用的Activity。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-07-30 11:58
 *
 * **版本:** v 1.0.0
 */
class CommonActivity : BasicActivity() {

    companion object {
        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         */
        inline fun <reified F : Fragment> launch(context: Context, @StringRes title: Int): Intent {
            return launch(context, F::class.java, getString(title)) {}
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         */
        fun launch(context: Context, target: Class<out Fragment>, @StringRes title: Int): Intent {
            return launch(context, target, getString(title)) {}
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun <reified F : Fragment> launch(context: Context, @StringRes title: Int, configurator: Intent.() -> Unit): Intent {
            return launch(context, F::class.java, getString(title), configurator)
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launch(context: Context, target: Class<out Fragment>, @StringRes title: Int, configurator: Intent.() -> Unit): Intent {
            return launch(context, target, getString(title), configurator)
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         */
        inline fun <reified F : Fragment> launch(context: Context, title: CharSequence? = null): Intent {
            return launch(context, F::class.java, title)
        }

        /**
         * 启动页面。
         * @param title 页面的标题。
         */
        fun launch(context: Context, target: Class<out Fragment>, title: CharSequence? = null): Intent {
            return launch(context, target, title) {}
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun <reified F : Fragment> launch(context: Context, title: CharSequence? = null, configurator: Intent.() -> Unit): Intent {
            return launch(context, F::class.java, title, configurator)
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launch(context: Context, target: Class<out Fragment>, title: CharSequence? = null, configurator: Intent.() -> Unit): Intent {
            return InnerIntent(context, CommonActivity::class.java).apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                setTarget(target)
                title?.also { title(title) }
                configurator(this)
                context.startActivity(this, launchOptions)
            }
        }

        /**
         * 通过配置启动页面，改方法不会自动拉起Activity，需要调用者在configurator中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun <reified F : Fragment> launchByOption(context: Context, @StringRes title: Int, configurator: Intent.() -> Unit): Intent {
            return launchByOption(context, F::class.java, getString(title), configurator)
        }

        /**
         * 通过配置启动页面，改方法不会自动拉起Activity，需要调用者在configurator中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launchByOption(context: Context, target: Class<out Fragment>, @StringRes title: Int, configurator: Intent.() -> Unit): Intent {
            return launchByOption(context, target, getString(title), configurator)
        }

        /**
         * 通过配置启动页面，改方法不会自动拉起Activity，需要调用者在configurator中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun <reified F : Fragment> launchByOption(context: Context, title: CharSequence? = null, configurator: Intent.() -> Unit): Intent {
            return launchByOption(context, F::class.java, title, configurator)
        }

        /**
         * 通过配置启动页面，改方法不会自动拉起Activity，需要调用者在configurator中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launchByOption(context: Context, target: Class<out Fragment>, title: CharSequence? = null, configurator: Intent.() -> Unit): Intent {
            return InnerIntent(context, CommonActivity::class.java).apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                title?.also { title(title) }
                setTarget(target)
                configurator(this)
            }
        }

        /**
         * 启动Tab页面。
         * @param scrollEnable ViewPager是否支持左右滑动翻页，默认为true。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launchTab(context: Context, scrollEnable: Boolean = true, configurator: Intent.() -> Unit): Intent {
            return InnerIntent(context, CommonActivity::class.java).apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                forTab(true, scrollEnable)
                configurator(this)
                context.startActivity(this, launchOptions)
            }
        }

        /**
         * 通过配置启动Tab页面，改方法不会自动拉起Activity，需要调用者在configurator中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param scrollEnable ViewPager是否支持左右滑动翻页，默认为true。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launchTabByOption(context: Context, scrollEnable: Boolean = true, configurator: Intent.() -> Unit): Intent {
            return InnerIntent(context, CommonActivity::class.java).apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                forTab(true, scrollEnable)
                configurator(this)
            }
        }

        /**
         * 启动Tab页面，只启动不配置页面样式。
         * @param immersion 是否启动沉浸式样式。
         * @param scrollEnable ViewPager是否支持左右滑动翻页，默认为true。
         * @param configuration 页面的适配器配置函数，用于在启动页面后对目标页面配置Tab。
         */
        fun launchTabOnly(context: Context, immersion: Boolean = false, scrollEnable: Boolean = true, configuration: CommonFragmentStatePagerAdapter.() -> Unit): Intent {
            return InnerIntent(context, CommonActivity::class.java).apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                forTab(true, scrollEnable)
                if (immersion) {
                    immersion()
                }
                configurePage(configuration)
                context.startActivity(this, launchOptions)
            }
        }
    }


    private fun getCurrentFragment(intent: Intent): Fragment {
        return FragmentProvider.provideFragment(intent) ?: onJumpError(SystemError.TARGET_PAGE_TYPE_NOT_HANDLER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isForTab = InnerIntent.isForTab(intent)
        if (isForTab) {
            setContentView(R.layout.kelin_ui_kit_activity_tablayout_toolbar)
            initTitleBar(getView(R.id.my_awesome_toolbar), null, null)
            setTabLayoutParamsAndAdapter()
        } else {
            setContentView(R.layout.kelin_ui_kit_activity_common)
            initTitleBar(getView(R.id.my_awesome_toolbar), getView(R.id.toolbar_center_title), getView(R.id.toolbar_sub_title))
            setCommonLayoutParams()
            title = intent.getCharSequenceExtra(BaseFragmentActivity.KEY_PAGE_TITLE)
            getCurrentFragment(intent).also {
                replaceFragment(R.id.flUiKitFragmentContainer, it)
                (it as? BasicFragment)?.isDarkMode?.also { isDark ->
                    if (isDark) {
                        StatusBarHelper.setStatusBarDarkMode(this)
                    } else {
                        StatusBarHelper.setStatusBarLightMode(this)
                    }
                }
            }
            getView<View>(R.id.toolbar_title_left_but)?.setOnClickListener { onBackPressed() }
        }
    }

    private fun setCommonLayoutParams() {
        (getView<View>(R.id.flUiKitFragmentContainer)?.layoutParams as? ConstraintLayout.LayoutParams)?.also { lp ->
            ((intent.getSerializableExtra(KEY_IMMERSION_MODE) as? ImmersionMode) ?: ImmersionMode.NONE).also { mode ->
                if (mode == ImmersionMode.NONE) {
                    lp.topToBottom = R.id.vUiKitToolbarLine
                    lp.topToTop = ConstraintLayout.LayoutParams.UNSET
                } else {
                    processStatusBar(Color.TRANSPARENT)
                    if (mode == ImmersionMode.NO_TOOLBAR) {
                        supportActionBar?.hide()
                    } else {
                        intent.getIntExtra(KEY_NAVIGATION_ICON, -1).takeIf { it > 0 }?.also {
                            setNavigationIcon(it)
                        }
                    }
                    lp.topToBottom = ConstraintLayout.LayoutParams.UNSET
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
        }
    }

    private fun setTabLayoutParamsAndAdapter() {
        getView<ViewPager>(R.id.uiKitVpPager)?.run {
            (this as? FixedViewPager)?.isUserInputEnable = InnerIntent.isTabScrollEnable(intent)
            (layoutParams as? ConstraintLayout.LayoutParams)?.also { lp ->
                ((intent.getSerializableExtra(KEY_IMMERSION_MODE) as? ImmersionMode) ?: ImmersionMode.NONE).also { mode ->
                    if (mode == ImmersionMode.NONE) {
                        lp.topToBottom = R.id.uiKitRlToolbarParent
                        lp.topToTop = ConstraintLayout.LayoutParams.UNSET
                    } else {
                        processStatusBar(Color.TRANSPARENT)
                        intent.getIntExtra(KEY_NAVIGATION_ICON, -1).takeIf { it > 0 }?.also {
                            setNavigationIcon(it)
                        }
                        lp.topToBottom = ConstraintLayout.LayoutParams.UNSET
                        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
            }
            val pagerAdapter = CommonFragmentStatePagerAdapter(supportFragmentManager).also {
                InnerIntent.getTabPageConfig(intent)?.configuration?.invoke(it) ?: NullPointerException("The configurationPage method must called!").printStackTrace()
                offscreenPageLimit = it.count
            }
            adapter = pagerAdapter
            val tabLayout = ViewPagerTabLayoutHelper.createCommonToolbarTabLayout(this)
            getView<MaxSizeRelativeLayout>(R.id.uiKitLlCustomCenterView)?.run {
                post {
                    setMaxWidth(DeviceUtil.getScreenWidth(applicationContext, 1.0) - (left shl 1))
                }
                val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 55.dp2px)
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                addView(tabLayout, lp)
            }
            currentItem = InnerIntent.getTabDefIndex(intent).let { if (it >= pagerAdapter.size) pagerAdapter.size - 1 else it }
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    initStatusBarMode(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })
            initStatusBarMode(currentItem)
        }
    }

    private fun ViewPager.initStatusBarMode(position: Int) {
        (adapter as? CommonFragmentStatePagerAdapter)?.also {
            it.getItem(position).isDarkMode?.also { dark ->
                if (dark) {
                    StatusBarHelper.setStatusBarDarkMode(this@CommonActivity)
                } else {
                    StatusBarHelper.setStatusBarLightMode(this@CommonActivity)
                }
            }
        }
    }
}