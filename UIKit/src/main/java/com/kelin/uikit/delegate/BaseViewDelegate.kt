package com.kelin.uikit.delegate

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.kelin.apiexception.ApiException
import com.kelin.logger.Logger
import com.kelin.proxyfactory.exception.ProxyLogicError
import com.kelin.uikit.R
import com.kelin.uikit.UIKit
import com.kelin.uikit.common.Immersive
import com.kelin.uikit.presenter.ViewPresenter
import com.kelin.uikit.tools.DoubleClickHandler
import com.kelin.uikit.tools.NetWorkStateUtil
import com.kelin.uikit.tools.text.SimpleTextWatch
import com.kelin.uikit.widget.statelayout.StatePage
import com.kelin.uikit.widget.statelayout.StatePageLayout

/**
 * **描述:** Delegate的基类，所有的Delegate都必须改类的派生类。
 *
 * **创建人:** kelin
 * **创建时间:** 2019/3/29  1:33 PM
 * **版本:** v 1.0.0
 */
abstract class BaseViewDelegate<VC : BaseViewDelegate.BaseViewDelegateCallback> : ViewDelegate<ViewPresenter<VC>>, Immersive {

    private val defaultLoadingOption: StateOption by lazy { LoadingStateOption() }
    private val defaultRetryOption: StateOption by lazy { RetryStateOption() }
    private val defaultEmptyOption: StateOption by lazy { EmptyStateOption() }
    private val doubleClickHandler by lazy { DoubleClickHandler() }

    final override var containerView: View? = null
        private set

    protected var viewPresenter: ViewPresenter<VC>? = null

    protected val viewCallback: VC by lazy { viewPresenter!!.viewCallback }

    private val mOnCLickListener: View.OnClickListener by lazy { InnerOnclickListener() }

    /**
     * 所有界面都必须有重试和加载的view
     */
    protected lateinit var statePage: StatePage
        private set

    /**
     * 获取页面状态标识，也就是说当前页面需要拥有哪些状态下的页面。多个状态用 "|" 符号连接。
     *
     * @return 返回当前页面拥有的状态，这里默认实现为拥有[StatePage.HAVE_EMPTY_STATE]和
     * [StatePage.HAVE_RETRY_STATE]。
     * @see StatePage.HAVE_RETRY_STATE
     *
     * @see StatePage.HAVE_EMPTY_STATE
     *
     * @see StatePage.HAVE_LOADING_STATE
     */
    open val pageStateFlags: Int
        get() = StatePage.NOTHING_STATE

    protected val context: Context
        get() = viewPresenter?.getContext() ?: throw NullPointerException("the viewPresenter is null object!")

    protected val applicationContext: Context
        get() = context.applicationContext

    @get:LayoutRes
    abstract val rootLayoutId: Int

    @get:IdRes
    protected open val dataViewId: Int
        get() = 0

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> onStart()
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_PAUSE -> onPause()
            Lifecycle.Event.ON_STOP -> onStop()
            Lifecycle.Event.ON_DESTROY -> onDestroy()
            else -> {
            }
        }
    }

    override fun onImmersion(offset: Int) {}

    protected open fun onResume() {
    }

    override fun onRealResume() {
    }

    protected open fun onStart() {
    }

    protected open fun onPause() {
    }

    override fun onRealPause() {
    }

    protected open fun onStop() {
    }

    protected open fun onDestroy() {
    }

    /**
     * show加载数据前的view
     * 3个互斥
     */
    fun showLoadingView() {
        statePage.showLoadingView()
    }

    /**
     * show数据加载成功之后的view
     * 3个互斥
     */
    fun showDataView() {
        statePage.showDataView()
    }

    fun showRetryView(e: ApiException? = null) {
        if (e?.errCode == ProxyLogicError.NETWORK_UNAVAILABLE.code || !NetWorkStateUtil.isConnected) {
            showRetryView(getString(R.string.poor_network_environment), R.drawable.img_network_disable)
        } else {
            showRetryView(e?.displayMessage ?: getString(R.string.load_error_click_retry), R.drawable.img_common_empty)
        }
    }

    fun showRetryView(@StringRes tip: Int?, @DrawableRes icon: Int = R.drawable.img_common_empty, @StringRes buttonText: Int? = null) {
        showRetryView(getString(tip ?: R.string.load_error_click_retry), icon, buttonText?.let { getString(buttonText) } ?: "")
    }

    fun showRetryView(tip: String?, @DrawableRes icon: Int = R.drawable.img_common_empty, buttonText: String = "") {
        val retryView = statePage.retryView
        if (retryView != null) {
            defaultRetryOption.icon = icon
            defaultRetryOption.title = tip ?: getString(R.string.load_error_click_retry)
            defaultRetryOption.btnText = buttonText
            refreshStatePageView(retryView, defaultRetryOption)
            statePage.showRetryView()
        }
    }

    fun showEmptyView() {
        statePage.showEmptyView()
    }

    @CallSuper
    open fun onCreate(presenter: ViewPresenter<VC>) {
        this.viewPresenter = presenter
    }

    @CallSuper
    open fun onPresenterDestroy() {
    }

    override fun presentView(viewPresenter: ViewPresenter<VC>, savedInstanceState: Bundle?) {}

    @CallSuper
    override fun bindView(viewPresenter: ViewPresenter<VC>, savedInstanceState: Bundle?) {
    }

    @CallSuper
    override fun unbindView() {
        this.viewPresenter = null
    }

    protected fun <V : View> getView(id: Int): V? {
        return containerView?.findViewById(id)
    }

    protected fun getString(@StringRes stringResId: Int, vararg formatArgs: Any): String {
        return (viewPresenter?.getContext() ?: UIKit.getContext()).resources.let {
            if (formatArgs.isEmpty()) {
                it.getString(stringResId)
            } else {
                it.getString(stringResId, *formatArgs)
            }
        }
    }

    protected fun getColor(@ColorRes colorId: Int): Int {
        return ContextCompat.getColor(viewPresenter?.getContext() ?: UIKit.getContext(), colorId)
    }

    protected fun getDimension(@DimenRes res: Int): Int {
        return (viewPresenter?.getContext() ?: UIKit.getContext()).resources.getDimension(res).toInt()
    }


    protected fun getColorStateList(@ColorRes colorStateListId: Int): ColorStateList {
        return AppCompatResources.getColorStateList(viewPresenter?.getContext() ?: UIKit.getContext(), colorStateListId)
    }

    protected fun getDrawable(@DrawableRes drawableId: Int): Drawable? {
        return if (drawableId == 0) null else AppCompatResources.getDrawable(viewPresenter?.getContext() ?: UIKit.getContext(), drawableId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val statePageLayout = StatePageLayout(container?.context ?: context)
        statePageLayout.isFocusable = false
        statePageLayout.isClickable = false

        val rootView = inflater.inflate(rootLayoutId, container, false)
        val dataView = rootView.findViewById<View>(dataViewId)
        containerView = if (dataView == null || dataView === rootView) {
            statePageLayout.apply {
                init(pageStateFlags, loadingStateLayout, retryStateLayout, emptyStateLayout, rootView)
            }
        } else {
            statePageLayout.init(pageStateFlags, loadingStateLayout, retryStateLayout, emptyStateLayout, dataView)
            rootView
        }
        this.statePage = statePageLayout
        statePageLayout.emptyView?.findViewById<View>(R.id.btnStatePageButton)?.setOnClickListener { onEmptyButtonClick() }
        statePageLayout.retryView?.findViewById<View>(R.id.btnStatePageButton)?.setOnClickListener { onRetryButtonClick() }
        refreshEmptyView()
        refreshLoadingView()
        statePageLayout.loadingView?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
                val loadingIcon = statePageLayout.loadingView?.findViewById<ImageView>(R.id.ivStatePageIcon)?.drawable
                if (loadingIcon != null && loadingIcon is AnimationDrawable) {
                    loadingIcon.start()
                }
            }

            override fun onViewDetachedFromWindow(v: View?) {
                val loadingIcon = statePageLayout.loadingView?.findViewById<ImageView>(R.id.ivStatePageIcon)?.drawable
                if (loadingIcon != null && loadingIcon is AnimationDrawable) {
                    loadingIcon.stop()
                }
            }

        })

        return containerView!!
    }

    private fun refreshLoadingView() {
        val stateView = statePage.loadingView
        if (stateView != null) {
            onConfigurationLoadingOption(defaultLoadingOption)
            refreshStatePageView(stateView, defaultLoadingOption)
        }
    }

    protected open fun onConfigurationLoadingOption(option: StateOption) {

    }

    protected fun refreshRetryView() {
        val stateView = statePage.retryView
        if (stateView != null) {
            onConfigurationRetryOption(defaultRetryOption)
            refreshStatePageView(stateView, defaultRetryOption)
        }
    }

    protected open fun onConfigurationRetryOption(option: StateOption) {

    }

    protected fun refreshEmptyView() {
        val stateView = statePage.emptyView
        if (stateView != null) {
            onConfigurationEmptyOption(defaultEmptyOption)
            refreshStatePageView(stateView, defaultEmptyOption)
        }
    }

    protected open fun onConfigurationEmptyOption(option: StateOption) {

    }

    protected open fun onEmptyButtonClick() {
    }


    protected open fun onRetryButtonClick() {
    }

    override fun restoreInstanceState(savedInstanceState: Bundle?) {}

    override fun saveInstanceState(outState: Bundle) {}

    override fun destroyView(outState: Bundle?) {
        containerView = null
    }

    override fun postDelayed(delayMillis: Long, runner: () -> Unit) {
        containerView?.postDelayed(runner, delayMillis) ?: Logger.system("BaseViewDelegate")?.e("containerView is Null Object!")
    }

    protected fun listenerTextChanged(vararg views: EditText) {
        if (views.isNotEmpty()) {
            for (view in views) {
                view.addTextChangedListener(object : SimpleTextWatch(view) {
                    override fun afterTextChanged(et: TextView, s: Editable) {
                        this@BaseViewDelegate.onTextChanged(et, et is EditText, s)
                    }
                })
            }
        }
    }

    protected open fun onTextChanged(v: TextView, isEditText: Boolean, text: Editable) {}

    fun bindClickEvent(vararg viewIds: Int) {
        bindClickEvent(*viewIds.toList().mapNotNull { getView(it) }.toTypedArray())
    }

    fun bindClickEvent(vararg views: View) {
        if (views.isNotEmpty()) {
            for (v in views) {
                v.setOnClickListener(mOnCLickListener)
            }
        }
    }


    protected open fun onViewClick(v: View) {}


    @get:LayoutRes
    protected open val loadingStateLayout: Int
        get() = R.layout.state_layout_loading

    @get:LayoutRes
    protected open val retryStateLayout: Int
        get() = R.layout.state_layout_retry

    @get:LayoutRes
    protected open val emptyStateLayout: Int
        get() = R.layout.state_layout_empty


    fun setDrawableTop(tv: TextView, @DrawableRes drawableRes: Int) {
        val drawable = getDrawable(drawableRes)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(null, drawable, null, null)
    }

    fun setDrawableStart(tv: TextView, @DrawableRes drawableRes: Int) {
        val drawable = getDrawable(drawableRes)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(drawable, null, null, null)
    }

    fun setDrawableEnd(tv: TextView, @DrawableRes drawableRes: Int) {
        val drawable = getDrawable(drawableRes)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(null, null, drawable, null)
    }

    fun setDrawableStartAndEnd(tv: TextView, @DrawableRes drawableStart: Int, @DrawableRes drawableEnd: Int) {
        val start = getDrawable(drawableStart)
        start?.setBounds(0, 0, start.minimumWidth, start.minimumHeight)
        val end = getDrawable(drawableEnd)
        end?.setBounds(0, 0, end.minimumWidth, end.minimumHeight)
        tv.setCompoundDrawables(start, null, end, null)
    }

    private inner class InnerOnclickListener : View.OnClickListener {
        override fun onClick(v: View) {
            //防止重复点击
            if (isNotFastClick(v)) {
                onViewClick(v)
            }
        }
    }

    protected fun isNotFastClick(v: View, duration: Long? = null) = doubleClickHandler.isNotFastClick(v, duration)

    open fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    interface BaseViewDelegateCallback : ViewDelegate.ViewDelegateCallback {
        fun finish() {}
    }

    companion object {
        fun refreshStatePageView(stateView: View, option: StateOption) {
            val iconView = stateView.findViewById<ImageView>(R.id.ivStatePageIcon)
            iconView?.visibility = if (option.icon == 0) {
                View.GONE
            } else {
                iconView?.setImageResource(option.icon)
                (iconView?.drawable as? AnimationDrawable)?.start()
                View.VISIBLE
            }
            val titleView = stateView.findViewById<TextView>(R.id.tvStatePageTitle)
            titleView?.visibility = if (option.title.isEmpty()) {
                View.GONE
            } else {
                titleView?.text = option.title
                if (option.titleColor != 0) {
                    titleView.setTextColor(option.titleColor)
                }
                View.VISIBLE
            }
            val subTitleView = stateView.findViewById<TextView>(R.id.tvStatePageSubTitle)
            subTitleView?.visibility = if (option.subTitle.isEmpty()) {
                View.GONE
            } else {
                subTitleView.text = option.subTitle
                if (option.subTitleColor != 0) {
                    subTitleView.setTextColor(option.subTitleColor)
                }
                View.VISIBLE
            }
            val button = stateView.findViewById<TextView>(R.id.btnStatePageButton)
            button?.visibility = if (option.btnText.isEmpty()) {
                View.GONE
            } else {
                button.text = option.btnText
                if (option.btnTextColor != 0) {
                    button.setTextColor(option.btnTextColor)
                }
                if (option.btnBackground != 0) {
                    button.setBackgroundResource(option.btnBackground)
                }
                View.VISIBLE
            }
        }
    }

    interface StateOption {
        var icon: Int

        var title: String

        var titleColor: Int

        var subTitle: String

        var subTitleColor: Int

        var btnText: String

        var btnTextColor: Int

        var btnBackground: Int
    }

    class SimpleStateOption(override var icon: Int, override var title: String, override var subTitle: String = "", override var btnText: String = "") : StateOption {

        override var titleColor: Int = 0

        override var subTitleColor: Int = 0

        override var btnTextColor: Int = 0

        override var btnBackground: Int = 0
    }

    private inner class LoadingStateOption : StateOption {

        override var icon: Int = R.drawable.anim_common_loading

        override var title: String = ""

        override var titleColor: Int = getColor(R.color.grey_999)

        override var subTitle: String = ""

        override var subTitleColor: Int = getColor(R.color.grey_999)

        override var btnText: String = ""

        override var btnTextColor: Int = getColor(android.R.color.white)

        override var btnBackground: Int = 0
    }

    private inner class RetryStateOption : StateOption {

        override var icon: Int = R.drawable.img_common_empty

        override var title: String = getString(R.string.load_error_click_retry)

        override var titleColor: Int = getColor(R.color.grey_999)

        override var subTitle: String = ""

        override var subTitleColor: Int = getColor(R.color.grey_999)

        override var btnText: String = ""

        override var btnTextColor: Int = getColor(android.R.color.white)

        override var btnBackground: Int = 0
    }

    private inner class EmptyStateOption : StateOption {

        override var icon: Int = R.drawable.img_common_empty

        override var title: String = getString(R.string.no_data)

        override var titleColor: Int = getColor(R.color.grey_999)

        override var subTitle: String = ""

        override var subTitleColor: Int = getColor(R.color.grey_999)

        override var btnText: String = ""

        override var btnTextColor: Int = getColor(android.R.color.white)

        override var btnBackground: Int = 0
    }
}
