package com.kelin.uikit.common.search

import java.io.Serializable


/**
 * 描述 描述有搜索功能的页面。
 * 创建人 kelin
 * 创建时间 2017/2/16  下午6:48
 * 版本 v 1.0.0
 */
interface SearchableHistoryPage : Serializable {
    /**
     * 初始化的对象。该方法的调用时机是对象被new出来以后，onCreate之前。
     */
    fun onInitSearchPage(searcher: Searcher)

    /**
     * 当需要搜索的时候调用。
     * @param searchKey 要搜索的关键字。
     */
    fun onSearch(searchKey: String)

    /**
     * 当取消搜索的按钮被点击后调用。
     */
    fun onSearchCancel()
}