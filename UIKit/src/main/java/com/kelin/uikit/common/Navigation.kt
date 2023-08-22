package com.kelin.uikit.common

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.kelin.uikit.*
import com.kelin.uikit.common.IOption.Companion.KEY_NAVIGATION_ICON
import com.kelin.uikit.common.IOption.Companion.KEY_NAVIGATION_TEXT
import com.kelin.uikit.common.h5.H5Delegate
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
        inline fun <reified F : Fragment> launch(context: Context, @StringRes title: Int, @ColorInt titleColor: Int? = null): Option {
            return launch(context, F::class.java, getString(title), titleColor) {}
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         */
        fun launch(context: Context, target: Class<out Fragment>, @StringRes title: Int, @ColorInt titleColor: Int? = null): Option {
            return launch(context, target, getString(title), titleColor) {}
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun <reified F : Fragment> launch(context: Context, @StringRes title: Int, @ColorInt titleColor: Int? = null, optional: Option.() -> Unit): Option {
            return launch(context, F::class.java, getString(title), titleColor, optional)
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launch(context: Context, target: Class<out Fragment>, @StringRes title: Int, @ColorInt titleColor: Int? = null, optional: Option.() -> Unit): Option {
            return launch(context, target, getString(title), titleColor, optional)
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         */
        inline fun <reified F : Fragment> launch(context: Context, title: CharSequence? = null, @ColorInt titleColor: Int? = null): Option {
            return launch(context, F::class.java, title, titleColor)
        }

        /**
         * 启动页面。
         * @param title 页面的标题。
         */
        fun launch(context: Context, target: Class<out Fragment>, title: CharSequence? = null, @ColorInt titleColor: Int? = null): Option {
            return launch(context, target, title, titleColor) {}
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun <reified F : Fragment> launch(context: Context, title: CharSequence? = null, @ColorInt titleColor: Int? = null, optional: Option.() -> Unit): Option {
            return launch(context, F::class.java, title, titleColor, optional)
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launch(context: Context, target: Class<out Fragment>, title: CharSequence? = null, @ColorInt titleColor: Int? = null, optional: Option.() -> Unit): Option {
            return IntentOption(context).apply {
                title?.also { title(it, titleColor) }
                setTarget(target)
                optional(this)
                start()
            }
        }

        /**
         * 启动Tab页面。
         * @param immersion 是否启动沉浸式样式。
         * @param slideEnable ViewPager是否支持左右滑动翻页，默认为true。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面配置及传参。
         */
        inline fun launchTab(context: Context, immersion: Boolean = false, slideEnable: Boolean = true, optional: TabOption.() -> Unit): TabOption {
            return TabIntentOption(context).apply {
                setSlideEnable(slideEnable)
                if (immersion) {
                    immersion()
                }
                optional(this)
                start()
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
                setSlideEnable(slideEnable)
                if (immersion) {
                    immersion()
                }
                pages(optional)
                start()
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
            return launchSearch(context, target) {
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
                @Suppress("UNCHECKED_CAST")
                setTarget(target as Class<out Fragment>)
                optional(this)
                start()
            }
        }

        /**
         * 启动一个H5页面。
         * @param url H5地址。
         * @param title 页面标题。
         */
        fun launchH5(context: Context, url: String, @StringRes title: Int, @ColorInt titleColor: Int? = null): H5Option {
            return launchH5(context, url, getString(title), titleColor)
        }

        /**
         * 启动一个H5页面。
         * @param url H5地址。
         * @param title 页面标题。
         */
        fun launchH5(context: Context, url: String, title: CharSequence? = null, @ColorInt titleColor: Int? = null): H5Option {
            return launchH5(context, url, title, titleColor) {}
        }

        /**
         * 启动一个H5页面。
         * @param url H5地址。
         * @param title 页面标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面进行配置。
         */
        inline fun launchH5(context: Context, url: String, title: CharSequence? = null, @ColorInt titleColor: Int? = null, optional: H5Option.() -> Unit): H5Option {
            return H5IntentOption(context).apply {
                h5url = url
                title?.also { title(it, titleColor) }
                optional(this)
                start()
            }
        }

        /**
         * 启动一个H5页面。
         * @param content H5页面的内容。
         * @param title 页面标题。
         */
        fun launchH5ByData(context: Context, content: String, @StringRes title: Int, @ColorInt titleColor: Int? = null): H5Option {
            return launchH5ByData(context, content, getString(title), titleColor)
        }

        /**
         * 启动一个H5页面。
         * @param content H5页面的内容。
         * @param title 页面标题。
         */
        fun launchH5ByData(context: Context, content: String, title: CharSequence? = null, @ColorInt titleColor: Int? = null): H5Option {
            return launchH5ByData(context, content, title, titleColor) {}
        }

        /**
         * 启动一个H5页面。
         * @param content H5页面的内容。
         * @param title 页面标题。
         * @param optional 页面的配置函数，用于在启动页面前对目标页面进行配置。
         */
        inline fun launchH5ByData(context: Context, content: String, title: CharSequence? = null, @ColorInt titleColor: Int? = null, optional: H5Option.() -> Unit): H5Option {
            return H5IntentOption(context).apply {
                h5Data = content
                title?.also { title(it, titleColor) }
                optional(this)
                start()
            }
        }
    }

    /**
     * 沉浸式状态栏模式。
     */
    private val immersionMode: ImmersionMode by lazy { IOption.getImmersionMode(intent) }

    /**
     * 判断当前页面是否是沉浸式状态栏。
     */
    val isImmersionMode: Boolean by lazy { immersionMode != ImmersionMode.NONE }

    private var searchDelegate: SearchPageDelegate? = null

    private var h5Delegate: H5Delegate? = null

    private fun getCurrentFragment(intent: Intent): Fragment {
        return IOption.getTargetFragment(intent) ?: onJumpError(SystemError.TARGET_PAGE_TYPE_NOT_HANDLER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val pageMode = IOption.getPageMode(intent)
        if (pageMode == PageMode.SEARCH && savedInstanceState != null) {
            // 不通过Android的自动恢复，因为onInitSearchKey,这个不会自动调用
            if (savedInstanceState.getParcelable<Parcelable>("android:support:fragments") != null) {
                savedInstanceState.remove("android:support:fragments")
            }
        }
        super.onCreate(savedInstanceState)
        //根据不同的页面模式处理不用的UI布局及样式。
        when (pageMode) {
            PageMode.NORMAL -> {
                setContentView(R.layout.kelin_ui_kit_activity_common)
                val titleView = getView<TextView>(R.id.toolbar_center_title)
                initTitleBar(getView(R.id.my_awesome_toolbar), titleView, getView(R.id.toolbar_sub_title))
                setToolbarStyle(getView(R.id.rlUiKitToolbarParent))
                setCommonLayoutParams()
                val centerTitle = intent.getBooleanExtra(IOption.KEY_PAGE_TITLE_CENTER, true)
                setTitle(intent.getCharSequenceExtra(IOption.KEY_PAGE_TITLE), centerTitle)
                try {
                    intent.getIntExtra(IOption.KEY_PAGE_TITLE_COLOR, -1).takeIf { it >= 0 }?.also {
                        if (centerTitle) {
                            titleView?.setTextColor(it)
                        } else {
                            titleColor = it
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
                setToolbarStyle(getView(R.id.uiKitRlToolbarParent))
                setTabLayoutParamsAndAdapter()
            }
            PageMode.SEARCH -> {
                setContentView(R.layout.kelin_ui_kit_activity_search)
                window.setSoftInputMode(SearchOption.getSoftInputMode(intent))
                setActionBarView(getView(R.id.rl_my_awesome_toolbar))
                setToolbarStyle(getView(R.id.rl_my_awesome_toolbar))
                setSearchLayoutParams()
                val searchPage = IOption.getTargetFragment(intent) as SearchablePage
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
            PageMode.H5 -> {
                setContentView(R.layout.kelin_ui_kit_activity_web_h5)
                val titleView = getView<TextView>(R.id.toolbar_center_title)
                initTitleBar(getView(R.id.my_awesome_toolbar), titleView, getView(R.id.toolbar_sub_title))
                setWebLayoutParams()
                val centerTitle = intent.getBooleanExtra(IOption.KEY_PAGE_TITLE_CENTER, true)
                setTitle(intent.getCharSequenceExtra(IOption.KEY_PAGE_TITLE), centerTitle)
                try {
                    intent.getIntExtra(IOption.KEY_PAGE_TITLE_COLOR, -1).takeIf { it >= 0 }?.also {
                        if (centerTitle) {
                            titleView?.setTextColor(it)
                        } else {
                            titleColor = it
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val webView = getView<WebView>(R.id.wbUiKitCommonWebView)!!
                h5Delegate = H5Delegate(
                    this,
                    webView,
                    H5Option.isCloseStyle(intent),
                    getView<View>(R.id.clUiKitCommonStateLayout)!!,
                    H5Option.getJavascriptInterface(intent)
                ) { view, request, error ->
                    processStatusBar(Color.WHITE, false)
                    getView<View>(R.id.rlUiKitToolbarParent)?.visibility = View.VISIBLE
                }
                setToolbarStyle(getView(R.id.rlUiKitToolbarParent))
                H5Option.getH5Data(intent).takeIf { !it.isNullOrBlank() }?.also { htmlContent ->
                    webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                } ?: webView.loadUrl(H5Option.getH5Url(intent))
                if (H5Option.isStatusBarDark(intent)) {
                    StatusBarHelper.setStatusBarDarkMode(this)
                } else {
                    StatusBarHelper.setStatusBarLightMode(this)
                }
            }
        }
    }

    /**
     * 设置状态栏样式。
     * @param toolbarContainer 状态栏的容器。
     */
    private fun setToolbarStyle(toolbarContainer: View?) {
        if (immersionMode != ImmersionMode.NO_TOOLBAR) {
            toolbarContainer?.also { v ->
                IOption.getToolbarColor(intent)?.also {
                    v.setBackgroundColor(it)
                } ?: IOption.getToolbarBackground(intent)?.also { bgRes ->
                    v.setBackgroundResource(bgRes)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return h5Delegate?.onKeyDown(keyCode, event) == true || super.onKeyDown(keyCode, event)
    }

    private fun setCommonLayoutParams() {
        (getView<View>(R.id.flUiKitFragmentContainer)?.layoutParams as? ConstraintLayout.LayoutParams)?.also { lp ->
            if (immersionMode == ImmersionMode.NONE) {
                lp.topToBottom = R.id.vUiKitToolbarLine
                lp.topToTop = ConstraintLayout.LayoutParams.UNSET
            } else {
                processStatusBar(Color.TRANSPARENT)
                lp.topToBottom = ConstraintLayout.LayoutParams.UNSET
                lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            if (immersionMode == ImmersionMode.NO_TOOLBAR) {
                supportActionBar?.hide()
            } else {
                setNavigationIcon()
            }
        }
    }

    private fun setWebLayoutParams() {
        (getView<View>(R.id.rlUiKitWebViewContainer)?.layoutParams as? ConstraintLayout.LayoutParams)?.also { lp ->
            if (immersionMode == ImmersionMode.NONE) {
                lp.topToBottom = R.id.rlUiKitToolbarParent
                lp.topToTop = ConstraintLayout.LayoutParams.UNSET
            } else {
                processStatusBar(Color.TRANSPARENT)
                lp.topToBottom = ConstraintLayout.LayoutParams.UNSET
                lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            if (immersionMode == ImmersionMode.NO_TOOLBAR) {
                supportActionBar?.hide()
            } else {
                setNavigationIcon()
            }
        }
    }

    private fun setSearchLayoutParams() {
        (getView<View>(R.id.flUiKitFragmentContainer)?.layoutParams as? ConstraintLayout.LayoutParams)?.also { lp ->
            if (immersionMode == ImmersionMode.NONE) {
                lp.topToBottom = R.id.rl_my_awesome_toolbar
                lp.topToTop = ConstraintLayout.LayoutParams.UNSET
            } else {
                processStatusBar(Color.TRANSPARENT)
                intent.getIntExtra(KEY_NAVIGATION_ICON, -1).takeIf { it >= 0 }?.also {
                    getView<ImageView>(R.id.ivUiKitNavigation)?.setImageResource(it)
                }
                lp.topToBottom = ConstraintLayout.LayoutParams.UNSET
                lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }
    }

    private fun setTabLayoutParamsAndAdapter() {
        getView<ViewPager>(R.id.uiKitVpPager)?.run {
            (this as? FixedViewPager)?.isUserInputEnable = TabOption.isTabSlideEnable(intent)
            (layoutParams as? ConstraintLayout.LayoutParams)?.also { lp ->
                if (immersionMode == ImmersionMode.NONE) {
                    lp.topToBottom = R.id.uiKitRlToolbarParent
                    lp.topToTop = ConstraintLayout.LayoutParams.UNSET
                } else {
                    processStatusBar(Color.TRANSPARENT)

                    lp.topToBottom = ConstraintLayout.LayoutParams.UNSET
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
            setNavigationIcon()
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

    private fun setNavigationIcon() {
        intent.getStringExtra(KEY_NAVIGATION_TEXT)?.takeIf { it.isNotBlank() }?.also {
            showTextNavigation(it)
        } ?: intent.getIntExtra(KEY_NAVIGATION_ICON, -1).takeIf { it >= 0 }?.also {
            setNavigationIcon(it)
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
        if ((searchDelegate?.onRecycle() ?: h5Delegate?.onPackPressed()) != true) {
            super.onBackPressed()
        }
    }
}