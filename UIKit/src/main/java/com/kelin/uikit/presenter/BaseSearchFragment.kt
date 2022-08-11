package com.kelin.uikit.presenter

import com.kelin.uikit.delegate.BaseSearchDelegate
import com.kelin.uikit.listcell.Cell


/**
 * **描述:** 通用搜索页面的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-8-13 21:00:39
 *
 * **版本:** v 1.0.0
 */
abstract class BaseSearchFragment<V : BaseSearchDelegate<VC, I>, VC : BaseSearchDelegate.BaseSearchDelegateCallback<I>, ID, I : Cell, W> : ItemListFragmentPresenter<V, VC, ID, I, W>() {

    abstract fun onSearchKeyChanged(searchKey: String)

    open inner class BaseSearchDelegateCallbackImpl : ItemListDelegateCallbackImpl(), BaseSearchDelegate.BaseSearchDelegateCallback<I> {

        override fun onSearchKeyChanged(searchKey: String) {
            this@BaseSearchFragment.onSearchKeyChanged(searchKey)
        }
    }
}
