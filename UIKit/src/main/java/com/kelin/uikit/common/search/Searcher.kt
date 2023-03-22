package com.kelin.uikit.common.search

/**
 * **描述: ** 搜索者。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2018/8/15  下午1:56
 *
 * **版本: ** v 1.0.0
 */
interface Searcher {
    /**
     * 搜索
     */
    fun search(searchKey: String?)

    /**
     * 现在搜索历史页面。
     */
    fun showHistory()
}