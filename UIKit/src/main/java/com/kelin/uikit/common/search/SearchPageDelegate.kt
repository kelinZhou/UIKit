package com.kelin.uikit.common.search

import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import com.kelin.uikit.common.Navigation
import com.kelin.uikit.tools.KeyBordUtil
import com.kelin.uikit.tools.text.TextWatchImpl
import com.kelin.uikit.visibleOrGone
import kotlinx.android.synthetic.main.kelin_ui_kit_activity_search.*

/**
 * **描述:** 搜索页面的代理。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/22 10:01 AM
 *
 * **版本:** v 1.0.0
 */
internal class SearchPageDelegate(
    private val owner: Navigation,
    initialSearch: Boolean,
    private val instantSearch: Boolean,
    private val searchPage: SearchablePage,
    private val searchHistoryPage: SearchableHistoryPage?,
    private val etSearch: EditText,
    private val ivClear: View
) : Searcher {

    private val keywordWatcher: TextWatchImpl by lazy {
        object : TextWatchImpl() {
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    ivClear.visibility = View.GONE
                } else {
                    ivClear.visibility = View.VISIBLE
                }
                if (instantSearch || s.isEmpty()) {
                    startSearch(s.toString())
                }
            }
        }
    }

    init {
        searchPage.onInitSearchPage(this)
        if (searchHistoryPage != null) {
            owner.switchSearchPage(searchHistoryPage as Fragment)
            searchHistoryPage.onInitSearchPage(this)
        } else if (initialSearch) {
            owner.switchSearchPage(searchPage as Fragment)
            searchPage.onSearch("")
        }
        etSearch.run {
            hint = searchPage.searchHint
            setOnKeyListener { v: View, keyCode: Int, event: KeyEvent ->
                if (event.action == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        val tv = v as TextView
                        val value = tv.text.toString().trim { it <= ' ' }
                        startSearch(value)
                        v.clearFocus()
                        KeyBordUtil.hideSoftKeyboard(v)
                        return@setOnKeyListener true
                    }
                }
                false
            }
            addTextChangedListener(keywordWatcher)
        }
        ivClear.run {
            setOnClickListener {
                setSearchValue("")
                KeyBordUtil.showSoftKeyboard(etSearch)
            }
        }
    }

    override fun search(searchKey: String?) {
        etSearch.run {
            removeTextChangedListener(keywordWatcher)
            KeyBordUtil.hideSoftKeyboard(this)
            setSearchValue(searchKey)
            ivClear.visibleOrGone = !searchKey.isNullOrEmpty()
            addTextChangedListener(keywordWatcher)
        }
        startSearch(searchKey ?: "")
    }

    override fun showHistory() {
        (searchHistoryPage as? Fragment)?.also { owner.switchSearchPage(it) }
    }

    /**
     * 设置搜索关键字。
     *
     * @param searchKey 要设置的关键字。
     */
    private fun setSearchValue(searchKey: String?) {
        etSearch.run {
            setText(searchKey)
            setSelection(if (searchKey.isNullOrEmpty()) 0 else searchKey.length)
        }
    }

    /**
     * 搜索
     *
     * @param key 要搜索的关键字。
     */
    private fun startSearch(key: String) {
        if (key.isBlank() && searchHistoryPage != null) {
            val fragment = searchPage as Fragment
            if (fragment.isAdded) {  // 如果有搜索页面正在显示则切换到搜索历史。
                owner.switchSearchPage(searchHistoryPage as Fragment)
            }
        } else {
            //如果关键字不是空的且不是即时搜索以及有搜索历史。
            if (!instantSearch && searchHistoryPage != null && key.isNotBlank()) {
                searchHistoryPage.onSearch(key) // 通知搜索历史页面添加搜索历史。
            }
            val fragment = searchPage as Fragment
            if (!fragment.isAdded) {  // 如果没有搜索结果页面没有被add。
                owner.switchSearchPage(fragment)
                etSearch.postDelayed(300) {
                    searchPage.onSearch(key)
                }
            } else {
                searchPage.onSearch(key)
            }
        }
    }

    fun onRecycle(): Boolean {
        KeyBordUtil.hideSoftKeyboard(etSearch)
        searchPage.onSearchCancel()
        searchHistoryPage?.onSearchCancel()
        return false
    }
}