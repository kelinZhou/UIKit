package com.kelin.uikit.delegate

import android.os.Bundle
import android.text.Editable
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.kelin.uikit.R
import com.kelin.uikit.listcell.Cell
import com.kelin.uikit.presenter.ViewPresenter
import com.kelin.uikit.tools.KeyBordUtil
import kotlinx.android.synthetic.main.common_list_search.*


/**
 * **描述:** 通用搜索页面的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-8-13 21:00:39
 *
 * **版本:** v 1.0.0
 */
abstract class BaseSearchDelegate<VC : BaseSearchDelegate.BaseSearchDelegateCallback<I>, I : Cell> : ItemListDelegate<VC, I>() {

    final override val rootLayoutId = R.layout.common_list_search


    override val dataViewId: Int
        get() = recyclerViewId

    abstract val inputHint: String

    override fun bindView(viewPresenter: ViewPresenter<VC>, savedInstanceState: Bundle?) {
        super.bindView(viewPresenter, savedInstanceState)
        etSearchKey.hint = inputHint
        listenerTextChanged(etSearchKey)
    }

    @CallSuper
    override fun onTextChanged(v: TextView, isEditText: Boolean, text: Editable) {
        if (v.id == R.id.etSearchKey) {
            viewCallback.onSearchKeyChanged(text.toString())
        }
    }

    @CallSuper
    override fun onRecyclerViewScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            KeyBordUtil.hideSoftKeyboard(etSearchKey)
        }
    }

    interface BaseSearchDelegateCallback<I : Cell> : ItemListDelegateCallback<I> {

        fun onSearchKeyChanged(searchKey: String)
    }
}
