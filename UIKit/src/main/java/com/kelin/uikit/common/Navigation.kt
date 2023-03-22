package com.kelin.uikit.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.*
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.kelin.uikit.*
import com.kelin.uikit.common.ImmersionMode.Companion.KEY_IMMERSION_MODE
import com.kelin.uikit.common.ImmersionMode.Companion.KEY_NAVIGATION_ICON
import com.kelin.uikit.common.search.SearchPageDelegate
import com.kelin.uikit.core.SystemError
import com.kelin.uikit.flyweight.adapter.CommonFragmentStatePagerAdapter
import com.kelin.uikit.common.search.SearchablePage
import com.kelin.uikit.tools.DeviceUtil
import com.kelin.uikit.tools.statusbar.StatusBarHelper
import com.kelin.uikit.widget.FixedViewPager
import com.kelin.uikit.widget.MaxSizeRelativeLayout
import com.kelin.uikit.widget.tablayout.helper.ViewPagerTabLayoutHelper
import kotlinx.android.synthetic.main.kelin_ui_kit_activity_search.*

/**
 * **描述:** 导航组件，可以使用该组件导航到任意一个Fragment并支持TabLayout样式、搜索样式以及支持从打开的页面获取数据。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-07-30 11:58
 *
 * **版本:** v 1.0.0
 */
class Navigation : BasicActivity() {

    companion object {
        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         */
        inline fun <reified F : Fragment> launch(context: Context, @StringRes title: Int): Option {
            return launch(context, F::class.java, getString(title)) {}
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         */
        fun launch(context: Context, target: Class<out Fragment>, @StringRes title: Int): Option {
            return launch(context, target, getString(title)) {}
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun <reified F : Fragment> launch(context: Context, @StringRes title: Int, optional: Option.() -> Unit): Option {
            return launch(context, F::class.java, getString(title), optional)
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launch(context: Context, target: Class<out Fragment>, @StringRes title: Int, optional: Option.() -> Unit): Option {
            return launch(context, target, getString(title), optional)
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         */
        inline fun <reified F : Fragment> launch(context: Context, title: CharSequence? = null): Option {
            return launch(context, F::class.java, title)
        }

        /**
         * 启动页面。
         * @param title 页面的标题。
         */
        fun launch(context: Context, target: Class<out Fragment>, title: CharSequence? = null): Option {
            return launch(context, target, title) {}
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun <reified F : Fragment> launch(context: Context, title: CharSequence? = null, optional: Option.() -> Unit): Option {
            return launch(context, F::class.java, title, optional)
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launch(context: Context, target: Class<out Fragment>, title: CharSequence? = null, optional: Option.() -> Unit): Option {
            return IntentOption(context).apply {
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                setTarget(target)
                title?.also { title(title) }
                optional(this)
                context.startActivity(intent, launchOptions)
            }
        }

        /**
         * 通过配置启动页面，该方法不会自动拉起Activity，需要调用者在optional中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun <reified F : Fragment> launchByOption(context: Context, @StringRes title: Int, optional: Option.() -> Unit): Option {
            return launchByOption(context, F::class.java, getString(title), optional)
        }

        /**
         * 通过配置启动页面，该方法不会自动拉起Activity，需要调用者在optional中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launchByOption(context: Context, target: Class<out Fragment>, @StringRes title: Int, optional: Option.() -> Unit): Option {
            return launchByOption(context, target, getString(title), optional)
        }

        /**
         * 通过配置启动页面，该方法不会自动拉起Activity，需要调用者在optional中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun <reified F : Fragment> launchByOption(context: Context, title: CharSequence? = null, optional: Option.() -> Unit): Option {
            return launchByOption(context, F::class.java, title, optional)
        }

        /**
         * 通过配置启动页面，该方法不会自动拉起Activity，需要调用者在optional中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launchByOption(context: Context, target: Class<out Fragment>, title: CharSequence? = null, optional: Option.() -> Unit): Option {
            return IntentOption(context).apply {
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                title?.also { title(title) }
                setTarget(target)
                optional(this)
            }
        }

        /**
         * 启动Tab页面。
         * @param slideEnable ViewPager是否支持左右滑动翻页，默认为true。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launchTab(context: Context, slideEnable: Boolean = true, optional: TabOption.() -> Unit): TabOption {
            return TabIntentOption(context).apply {
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                setSlideEnable(slideEnable)
                optional(this)
                context.startActivity(intent, launchOptions)
            }
        }

        /**
         * 通过配置启动Tab页面，该方法不会自动拉起Activity，需要调用者在optional中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param slideEnable ViewPager是否支持左右滑动翻页，默认为true。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launchTabByOption(context: Context, slideEnable: Boolean = true, optional: TabOption.() -> Unit): TabOption {
            return TabIntentOption(context).apply {
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                setSlideEnable(slideEnable)
                optional(this)
            }
        }

        /**
         * 启动Tab页面，只启动不配置页面样式。
         * @param immersion 是否启动沉浸式样式。
         * @param slideEnable ViewPager是否支持左右滑动翻页，默认为true。
         * @param optional 页面的适配器配置函数，用于在启动页面后对目标页面配置Tab。
         */
        fun launchTabOnly(context: Context, immersion: Boolean = false, slideEnable: Boolean = true, optional: CommonFragmentStatePagerAdapter.() -> Unit): TabOption {
            return TabIntentOption(context).apply {
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                setSlideEnable(slideEnable)
                if (immersion) {
                    immersion()
                }
                configurePage(optional)
                context.startActivity(intent, launchOptions)
            }
        }

        /**
         * 通过配置启动搜索页面。
         * @param F 目标Fragment：本次要启动的Fragment。
         * @param initialSearch 启动搜索页面后是否直接将搜索结果页面加载。
         * @param instantSearch 是否开启实时搜索，即用户每输入一个字符就触发一次搜索。
         */
        inline fun <reified F : SearchablePage> launchSearch(context: Context, initialSearch: Boolean = true, instantSearch: Boolean = true): SearchOption {
            return launchSearch(context, F::class.java, initialSearch, instantSearch)
        }

        /**
         * 通过配置启动搜索页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param initialSearch 启动搜索页面后是否直接将搜索结果页面加载。
         * @param instantSearch 是否开启实时搜索，即用户每输入一个字符就触发一次搜索。
         */
        fun launchSearch(context: Context, target: Class<out SearchablePage>, initialSearch: Boolean = true, instantSearch: Boolean = true): SearchOption {
            return launchSearch(context, target){
                initialSearch(initialSearch)
                instantSearch(instantSearch)
            }
        }

        /**
         * 通过配置启动搜索页面。
         * @param F 目标Fragment：本次要启动的Fragment。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun <reified F : SearchablePage> launchSearch(context: Context, optional: SearchOption.() -> Unit): SearchOption {
            return launchSearch(context, F::class.java, optional)
        }

        /**
         * 通过配置启动搜索页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launchSearch(context: Context, target: Class<out SearchablePage>, optional: SearchOption.() -> Unit): SearchOption {
            return SearchIntentOption(context).apply {
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                @Suppress("UNCHECKED_CAST")
                setTarget(target as Class<out Fragment>)
                optional(this)
                context.startActivity(intent, launchOptions)
            }
        }

        /**
         * 通过配置启动搜索页面。该方法不会自动拉起Activity，需要调用者在optional中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param F 目标Fragment：本次要启动的Fragment。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun <reified F : SearchablePage> launchSearchByOption(context: Context, optional: SearchOption.() -> Unit): SearchOption {
            return launchSearchByOption(context, F::class.java, optional)
        }

        /**
         * 通过配置启动搜索页面。该方法不会自动拉起Activity，需要调用者在optional中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launchSearchByOption(context: Context, target:Class<out SearchablePage>, optional: SearchOption.() -> Unit): SearchOption {
            return SearchIntentOption(context).apply {
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                @Suppress("UNCHECKED_CAST")
                setTarget(target as Class<out Fragment>)
                optional(this)
            }
        }
    }

    private var searchDelegate: SearchPageDelegate? = null

    private fun getCurrentFragment(intent: Intent): Fragment {
        return Option.getTargetFragment(intent) ?: onJumpError(SystemError.TARGET_PAGE_TYPE_NOT_HANDLER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val pageMode = Option.getPageMode(intent)
        if (pageMode == PageMode.SEARCH && savedInstanceState != null) {
            // 不通过Android的自动恢复，因为onInitSearchKey,这个不会自动调用
            val p = savedInstanceState.getParcelable<Parcelable>("android:support:fragments")
            if (p != null) {
                savedInstanceState.remove("android:support:fragments")
            }
        }
        super.onCreate(savedInstanceState)
        when (pageMode) {
            PageMode.NORMAL -> {
                setContentView(R.layout.kelin_ui_kit_activity_common)
                initTitleBar(getView(R.id.my_awesome_toolbar), getView(R.id.toolbar_center_title), getView(R.id.toolbar_sub_title))
                setCommonLayoutParams()
                title = intent.getCharSequenceExtra(Option.KEY_PAGE_TITLE)
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
            PageMode.TAB -> {
                setContentView(R.layout.kelin_ui_kit_activity_tablayout_toolbar)
                initTitleBar(getView(R.id.my_awesome_toolbar), null, null)
                setTabLayoutParamsAndAdapter()
            }
            PageMode.SEARCH -> {
                setContentView(R.layout.kelin_ui_kit_activity_search)
                window.setSoftInputMode(SearchOption.getSoftInputMode(intent))
                setActionBarView(getView(R.id.rl_my_awesome_toolbar))
                setSearchLayoutParams()
                val searchPage = Option.getTargetFragment(intent) as SearchablePage
                (searchPage as? BasicFragment)?.isDarkMode?.also { isDark ->
                    if (isDark) {
                        StatusBarHelper.setStatusBarDarkMode(this)
                    } else {
                        StatusBarHelper.setStatusBarLightMode(this)
                    }
                }
                getView<View>(R.id.tvUiKitCancelButton)?.setOnClickListener {
                    finish()
                }
                searchDelegate = SearchPageDelegate(
                    this,
                    SearchOption.isInitialSearch(intent),
                    SearchOption.isInstantSearch(intent),
                    searchPage,
                    SearchOption.getHistoryPage(intent),
                    getView<EditText>(R.id.etUiKitSearch)!!,
                    getView<View>(R.id.ivUiKitClear)!!
                )
            }
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

    private fun setSearchLayoutParams() {
        (getView<View>(R.id.flUiKitFragmentContainer)?.layoutParams as? ConstraintLayout.LayoutParams)?.also { lp ->
            ((intent.getSerializableExtra(KEY_IMMERSION_MODE) as? ImmersionMode) ?: ImmersionMode.NONE).also { mode ->
                if (mode == ImmersionMode.NONE) {
                    lp.topToBottom = R.id.rl_my_awesome_toolbar
                    lp.topToTop = ConstraintLayout.LayoutParams.UNSET
                } else {
                    processStatusBar(Color.TRANSPARENT)
                    intent.getIntExtra(KEY_NAVIGATION_ICON, -1).takeIf { it > 0 }?.also {
                        getView<ImageView>(R.id.ivUiKitNavigation)?.setImageResource(it)
                    }
                    lp.topToBottom = ConstraintLayout.LayoutParams.UNSET
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
        }
    }

    private fun setTabLayoutParamsAndAdapter() {
        getView<ViewPager>(R.id.uiKitVpPager)?.run {
            (this as? FixedViewPager)?.isUserInputEnable = TabOption.isTabSlideEnable(intent)
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
                TabOption.getTabPageConfig(intent)?.optional?.invoke(it) ?: NullPointerException("The configurePage method must called!").printStackTrace()
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
            currentItem = TabOption.getTabDefIndex(intent).let { if (it >= pagerAdapter.size) pagerAdapter.size - 1 else it }
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
                    StatusBarHelper.setStatusBarDarkMode(this@Navigation)
                } else {
                    StatusBarHelper.setStatusBarLightMode(this@Navigation)
                }
            }
        }
    }

    internal fun switchSearchPage(fragment: Fragment) {
        replaceFragment(R.id.flUiKitFragmentContainer, fragment)
    }

    override fun onBackPressed() {
        searchDelegate?.onRecycle()
        super.onBackPressed()
    }
}