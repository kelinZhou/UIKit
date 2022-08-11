package com.kelin.uikit.presenter

import com.kelin.uikit.presenter.BaseFragmentPresenter
import com.kelin.uikit.delegate.BaseViewPagerWrapperDelegate
import com.kelin.uikit.flyweight.adapter.CommonFragmentStatePagerAdapter


/**
 * **描述:** ViewPage页面的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-08-16 13:11:03
 *
 * **版本:** v 1.0.0
 */
abstract class BaseViewPagerWrapperFragment<V : BaseViewPagerWrapperDelegate<VC>, VC : BaseViewPagerWrapperDelegate.BaseViewPagerWrapperDelegateCallback> : BaseFragmentPresenter<V, VC>() {

    abstract fun configureViewPagerAdapter(adapter: CommonFragmentStatePagerAdapter)

    protected open fun onPagerNumberChanged(position: Int) {}

    open inner class BaseViewPagerWrapperDelegateCallbackImpl : BaseViewPagerWrapperDelegate.BaseViewPagerWrapperDelegateCallback {
        override fun onPagerNumberChanged(position: Int) {
            this@BaseViewPagerWrapperFragment.onPagerNumberChanged(position)
        }

        override fun onCreateViewPagerAdapter(): CommonFragmentStatePagerAdapter {
            val adapter = CommonFragmentStatePagerAdapter(childFragmentManager)
            configureViewPagerAdapter(adapter)
            return adapter
        }
    }
}
