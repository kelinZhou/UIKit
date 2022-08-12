package com.kelin.uikit.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kelin.uikit.BasicActivity
import com.kelin.uikit.R
import com.kelin.uikit.tools.KeyBordUtil.hideSoftKeyboard
import com.kelin.uikit.tools.KeyBordUtil.showSoftKeyboard
import com.kelin.uikit.tools.statusbar.StatusBarHelper
import com.kelin.uikit.tools.text.TextWatchImpl
import com.kelin.uikit.visibleOrGone
import kotlinx.android.synthetic.main.activity_search.*

/**
 * 描述 搜索页面。
 * 创建人 kelin
 * 创建时间 2017/2/7  下午4:23
 * 版本 v 1.0.0
 */
class SearchActivity : BasicActivity(), Searcher {

    companion object {
        private const val KEY_ARGUMENTS = "key_arguments"
        private const val KEY_INITIAL_SEARCH = "key_initial_search"
        private const val KEY_INSTANT_SEARCH = "key_instant_search"

        /**
         * 搜索历史页面的字节码对象。
         */
        private var sSearchHistoryPageClass: Class<out SearchablePage>? = null

        /**
         * 搜索页面的字节码对象。
         */
        private var sSearchPageClass: Class<out SearchablePage>? = null
        fun startSearchPage(context: Context, cls: Class<out SearchablePage>, arg: Bundle? = null, initialSearch: Boolean = true, instantSearch: Boolean = true) {
            startSearchPage(context, cls, null, arg, initialSearch, instantSearch)
        }

        /**
         * 启动搜索页面。
         *
         * @param context              上下文。
         * @param cls                  搜索结果页面。
         * @param searchHistoryPageCls 搜索历史页面。
         * @param arg                  搜索结果和搜索历史页面的数据捆。
         * @param initialSearch        启动搜索页面后是否直接将搜索结果页面加载。
         * @param instantSearch        是否即时搜索。
         */
        fun startSearchPage(context: Context, cls: Class<out SearchablePage>, searchHistoryPageCls: Class<out SearchablePage>?, arg: Bundle?, initialSearch: Boolean, instantSearch: Boolean) {
            context.startActivity(getSearchPageIntent(context, cls, searchHistoryPageCls, arg, initialSearch, instantSearch))
        }

        /**
         * 启动搜索页面。
         *
         * @param context              上下文。
         * @param cls                  搜索结果页面。
         * @param searchHistoryPageCls 搜索历史页面。
         * @param arg                  搜索结果和搜索历史页面的数据捆。
         * @param initialSearch        启动搜索页面后是否直接将搜索结果页面加载。
         * @param instantSearch        是否即时搜索。
         */
        fun getSearchPageIntent(
            context: Context,
            cls: Class<out SearchablePage>,
            searchHistoryPageCls: Class<out SearchablePage>?,
            arg: Bundle? = null,
            initialSearch: Boolean = true,
            instantSearch: Boolean = searchHistoryPageCls == null
        ): Intent {
            sSearchPageClass = cls
            sSearchHistoryPageClass = searchHistoryPageCls
            return getIntent(arg, initialSearch, instantSearch, context)
        }

        private fun getIntent(arg: Bundle?, initialSearch: Boolean, instantSearch: Boolean, context: Context?): Intent {
            val intent = Intent(context, SearchActivity::class.java)
            if (arg != null) {
                intent.putExtra(KEY_ARGUMENTS, arg)
            }
            intent.putExtra(KEY_INITIAL_SEARCH, initialSearch)
            intent.putExtra(KEY_INSTANT_SEARCH, instantSearch)
            return intent
        }
    }

    /**
     * 搜索历史页面。
     */
    private var searchHistoryPage: SearchablePage? = null

    /**
     * 搜索页面。
     */
    private var searchPage: SearchablePage? = null
    private var mCurSearchKey: String? = null
    private val mSearchRunnable = Runnable { searchPage!!.onSearch(mCurSearchKey!!) }
    private var instantSearch = false
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

    override fun onBackPressed() {
        hideSoftKeyboard(etSearch)
        if (searchPage != null) {
            searchPage!!.onSearchCancel()
        } else {
            searchHistoryPage!!.onSearchCancel()
        }
        super.onBackPressed()
    }

    override fun overrideWindowSoftInputModeEnable(): Boolean {
        return false
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            // 不通过Android的自动恢复，因为onInitSearchKey,这个不会自动调用
            val FRAGMENTS_TAG = "android:support:fragments"
            val p = savedInstanceState.getParcelable<Parcelable>(FRAGMENTS_TAG)
            if (p != null) {
                savedInstanceState.remove(FRAGMENTS_TAG)
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        val intent = intent
        val initialSearch = intent.getBooleanExtra(KEY_INITIAL_SEARCH, false)
        instantSearch = intent.getBooleanExtra(KEY_INSTANT_SEARCH, false)
        val searchHint: String
        when {
            sSearchHistoryPageClass != null -> {
                searchHistoryPage = newInstance(sSearchHistoryPageClass!!)
                searchHint = searchHistoryPage!!.searchHint
                addFragment(warpFragmentId, (searchHistoryPage as Fragment?)!!)
                searchHistoryPage!!.onInitSearchPage(this)
            }
            sSearchPageClass != null -> {
                searchPage = newInstance(sSearchPageClass!!)
                searchHint = searchPage!!.searchHint
                if (initialSearch) {
                    addFragment(warpFragmentId, (searchPage as Fragment?)!!)
                    searchPage?.apply {
                        onInitSearchPage(this@SearchActivity)
                        onSearch("")
                    }
                }
            }
            else -> {
                NullPointerException("The sSearchHistoryPageClass or sSearchPageClass must not be null!").printStackTrace()
                finish()
                searchHint = ""
            }
        }
        etSearch.run {
            hint = searchHint
            setOnKeyListener { v: View, keyCode: Int, event: KeyEvent ->
                if (event.action == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        val tv = v as TextView
                        val value = tv.text.toString().trim { it <= ' ' }
                        startSearch(value)
                        v.clearFocus()
                        hideSoftKeyboard(v)
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
                showSoftKeyboard(etSearch)
            }
        }
        if (tvOperation != null) {
            tvOperation.setOnClickListener {
                finish()
            }
        }
        StatusBarHelper.setStatusBarLightMode(this)
    }

    override fun search(searchKey: String?) {
        etSearch.run {
            removeTextChangedListener(keywordWatcher)
            hideSoftKeyboard(this)
            setSearchValue(searchKey)
            ivClear.visibleOrGone = !searchKey.isNullOrEmpty()
            addTextChangedListener(keywordWatcher)
        }
        startSearch(searchKey ?: "")
    }

    override fun showHistory() {
        replaceFragment(warpFragmentId, searchHistoryPage as Fragment)
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
        mCurSearchKey = key
        //如果是即时搜索则不保存搜索历史。
        if (!instantSearch && searchHistoryPage != null) {
            searchHistoryPage!!.onSearch(key) // 通知搜索历史页面添加搜索历史。
        }
        if (searchPage == null) {  //如果searchPage为null说明有搜索历史。
            searchPage = newInstance(sSearchPageClass!!)
            val fragment = searchPage as Fragment?
            replaceFragment(warpFragmentId, fragment!!)
            searchPage!!.onInitSearchPage(this)
            etSearch.postDelayed(mSearchRunnable, 300)
        } else {
            val fragment = searchPage as Fragment
            if (!fragment.isAdded) {  // 如果没有搜索历史且搜索结果页面没有被add。
                replaceFragment(warpFragmentId, fragment)
                etSearch.postDelayed(mSearchRunnable, 300)
            } else {
                searchPage!!.onSearch(key)
            }
        }
    }

    private fun newInstance(cls: Class<out SearchablePage>): SearchablePage {
        return try {
            cls.newInstance().also {
                if (it !is Fragment) {
                    throw ClassCastException("the SearchablePage:" + cls.simpleName + " must be a Fragment!")
                }
                (it as? Fragment)?.arguments = intent.getBundleExtra(KEY_ARGUMENTS)
            }
        } catch (e: Exception) {
            throw RuntimeException("实例化失败:" + e.message, e)
        }
    }

    private val warpFragmentId: Int
        get() = R.id.fragment_container
}