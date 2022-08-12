package com.kelin.uikit.presenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IntDef
import com.kelin.apiexception.ApiException
import com.kelin.proxyfactory.ActionParameter
import com.kelin.proxyfactory.IdActionDataProxy
import com.kelin.proxyfactory.LoadAction
import com.kelin.proxyfactory.ProxyFactory
import com.kelin.uikit.UIKit
import com.kelin.uikit.delegate.CommonViewDelegate
import com.kelin.uikit.event.NetworkEvent
import com.kelin.uikit.widget.statelayout.StatePage
import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.annotation.Inherited


/**
 * **描述:** 可以初始化数据并且有页面状态的Presenter。
 *
 * **创建人:** kelin
 * **创建时间:** 2019/3/29  1:33 PM
 * **版本:** v 1.0.0
 */
abstract class CommonFragmentPresenter<V : CommonViewDelegate<VC, VD>, VC : CommonViewDelegate.CommonViewDelegateCallback, ID, D, VD> : BaseFragmentPresenter<V, VC>() {

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.PROPERTY_GETTER)
    @MustBeDocumented
    @Inherited
    @IntDef(SETUP_DEFAULT, SETUP_TIMELY, SETUP_WITHOUT)
    annotation class SetupMode

    protected open val initialDataProxy: IdActionDataProxy<ID, D> by lazy {
        ProxyFactory.createIdActionProxy { getApiObservable(it) }
    }
    protected var initialData: D? = null
        set(value) {
            if (field == null) { //防止多次调用
                onInitialDataReady()
            }
            field = value
        }

    protected open fun onInitialDataReady() {}

    protected var initialViewData: VD? = null
    protected open val actionParameter: ActionParameter by lazy { ActionParameter.createInstance() }

    @get:SetupMode
    protected open val setupMode: Int = SETUP_DEFAULT

    /**
     * 创建proxy的请求id(请求参数对象)
     */
    abstract val initialRequestId: ID

    protected val isProxyWorking: Boolean
        get() = setupMode != SETUP_NO_PROXY && initialDataProxy.isWorking

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindInitProxy()
    }

    protected open fun onBindInitProxy() {
        initialDataProxy.bind(viewLifecycleOwner, onCreateDataCallback())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (setupMode != SETUP_NO_PROXY) {
            onSetup()
        }
    }

    protected open fun onSetup() {
        if (setupMode == SETUP_DEFAULT) {
            setup()
        }
    }

    override fun onRealResume() {
        super.onRealResume()
        if (setupMode == SETUP_TIMELY) {
            resumeLoadInitialData()
        }
    }

    override fun onResumeShowDataView() {
        if (saveDelegate && initialData != null) {
            viewDelegate?.also { vd ->
                if (!checkDataIsEmpty(initialData!!) && StatePage.haveState(vd.pageStateFlags, StatePage.HAVE_EMPTY_STATE)) {
                    vd.showDataView()
                } else {
                    vd.showEmptyView()
                }
            }
        }
    }

    private fun resumeLoadInitialData(showProgress: Boolean = false) {
        if (initialData == null) {
            loadInitialData()
        } else {
            onRefreshData(showProgress, LoadAction.AUTO_REFRESH)
        }
    }

    protected open fun setup() {
        if (initialData != null) {
            viewDelegate?.also { vd ->
                if (checkDataIsEmpty(initialData!!) && StatePage.haveState(vd.pageStateFlags, StatePage.HAVE_EMPTY_STATE)) {
                    vd.showEmptyView()
                    initialViewData = null
                } else {
                    vd.showDataView()
                    vd.setInitialData(transformUIData(initialData!!).also { initialViewData = it })
                }
            }
        } else {
            loadInitialData()
        }
    }

    protected fun loadInitialData(id: ID? = null) {
        onLoadInitData(id, LoadAction.LOAD)
    }

    protected fun reloadInitialData() {
        onLoadInitData(null, LoadAction.RETRY)
    }

    protected open fun onLoadInitData(id: ID? = null, loadAction: LoadAction) {
        if (setupMode != SETUP_NO_PROXY) {
            if (!saveDelegate || initialData == null) {
                viewDelegate?.showLoadingView()
            }
            initialDataProxy.request(actionParameter.updateAction(loadAction), id ?: initialRequestId)
        } else {
            throw RuntimeException("The Setup Mode is SETUP_NO_PROXY")
        }
    }

    protected fun refreshData(showProgress: Boolean = false) {
        onRefreshData(showProgress, LoadAction.REFRESH)
    }

    protected fun refreshData(data: D) {
        disposeInitData(data)
    }

    protected open fun onRefreshData(showProgress: Boolean, action: LoadAction) {
        if (setupMode != SETUP_NO_PROXY) {
            if (showProgress) {
                UIKit.toaster.showProgress(context)
            }
            initialDataProxy.request(actionParameter.updateAction(action), initialRequestId)
        } else {
            throw RuntimeException("The Setup Mode is SETUP_NO_PROXY")
        }
    }

    protected abstract fun getApiObservable(id: ID): Observable<D>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!saveDelegate) {
            initialData = null
        }
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveNetworkEvent(event: NetworkEvent) {
        onNetWorkStateChanged(event.networkType, event.isConnect)
    }

    /**
     * 加载数据的回调
     */
    protected open fun <ACTION : ActionParameter> onCreateDataCallback(): IdActionDataProxy.IdActionDataCallback<ID, ACTION, D> {
        return CommonDataLoadCallbackImpl()
    }

    @CallSuper
    protected open fun onNetWorkStateChanged(type: Int, isConnect: Boolean) {
        if (isConnect) {
            refreshData(true)
        }
    }


    @Suppress("UNCHECKED_CAST")
    protected open fun transformUIData(data: D): VD {
        return data as VD
    }

    /**
     * 拦截网络加载成功的回调。
     */
    protected open fun onInterceptLoadSuccess(id: ID, act: ActionParameter, d: D): Boolean {
        return false
    }

    /**
     * 拦截网络加载失败的回调。
     */
    protected open fun onInterceptLoadFailed(id: ID, act: ActionParameter, e: ApiException): Boolean {
        return false
    }

    /**
     * 判断数据是否为空。
     *
     * @param data 当前从网络获取到的数据。
     * @return 如果返回 `true` 当前数据是为空。
     */
    protected open fun checkDataIsEmpty(data: D): Boolean {
        return if (data is Collection<*>) data.isEmpty() else false
    }

    abstract inner class CommonDelegateCallbackImpl : CommonViewDelegate.CommonViewDelegateCallback {

        override fun onRetry() {
            val v = viewDelegate
            v!!.showLoadingView()
            reloadInitialData()
        }

        override fun onRefresh(showProgress: Boolean) {
            refreshData(showProgress)
        }
    }


    open inner class CommonDataLoadCallbackImpl<ACTION : ActionParameter> :
        IdActionDataProxy.DefaultIdDataCallback<ID, ACTION, D> {

        override fun onSuccess(id: ID, action: ACTION, data: D) {
            if (action.action == LoadAction.REFRESH || action.action == LoadAction.AUTO_REFRESH) {
                UIKit.toaster.hideProgress(context)
            }
            if (!onInterceptLoadSuccess(id, action, data)) {
                when (action.action) {
                    LoadAction.LOAD -> onLoadSuccess(id, data)
                    LoadAction.RETRY -> onRetrySuccess(id, data)
                    LoadAction.REFRESH, LoadAction.AUTO_REFRESH -> onRefreshSuccess(id, data)
                    else -> throw java.lang.RuntimeException("the action: ${action.action} not handler!")
                }
            }
        }

        override fun onFailed(id: ID, action: ACTION, e: ApiException) {
            if (action.action == LoadAction.REFRESH || action.action == LoadAction.AUTO_REFRESH) {
                UIKit.toaster.hideProgress(context)
            }
            if (!onInterceptLoadFailed(id, action, e)) {
                when (action.action) {
                    LoadAction.LOAD -> onLoadFailed(id, e)
                    LoadAction.RETRY -> onRetryFailed(id, e)
                    LoadAction.REFRESH, LoadAction.AUTO_REFRESH -> onRefreshFailed(id, e)
                    else -> throw java.lang.RuntimeException("the action: ${action.action} not handler!")
                }
            }
        }

        override fun onLoadSuccess(id: ID, data: D) {
            disposeInitData(data)
        }

        override fun onLoadFailed(id: ID, e: ApiException) {
            val v = viewDelegate ?: return
            v.showRetryView(e)
        }

        override fun onRefreshSuccess(id: ID, data: D) {
            onLoadSuccess(id, data)
            viewDelegate?.reSetRefresh()
        }

        override fun onRefreshFailed(id: ID, e: ApiException) {
            onLoadFailed(id, e)
        }

        override fun onRetrySuccess(id: ID, data: D) {
            onLoadSuccess(id, data)
        }

        override fun onRetryFailed(id: ID, e: ApiException) {
            onLoadFailed(id, e)
        }
    }

    private fun disposeInitData(data: D) {
        initialData = data
        viewDelegate?.also { vd ->
            if (checkDataIsEmpty(data) && StatePage.haveState(vd.pageStateFlags, StatePage.HAVE_EMPTY_STATE)) {
                vd.showEmptyView()
                initialViewData = null
            } else {
                vd.showDataView()
                vd.setInitialData(transformUIData(data).also { initialViewData = it })
            }
        }
    }

    companion object {
        /**
         * 默认的，只在页面加载时刷新一次页面。
         */
        internal const val SETUP_DEFAULT = 0x011

        /**
         * 实时刷新每次页面由不可见变为可见时都刷新页面。
         */
        internal const val SETUP_TIMELY = 0x012

        /**
         * 不需要自动请求网络数据，何时请求数据由子类自己决定。
         */
        internal const val SETUP_WITHOUT = 0x013

        /**
         * 没有网络代理，表示改页面从始至终都不可能请求网络，所以根本不需要创建代理对象。
         */
        internal const val SETUP_NO_PROXY = 0x014


        val defaultAny: Any by lazy { Any() }
    }
}
