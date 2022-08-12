package com.kelin.uikit.search


/**
 * 描述 描述有搜索功能的页面。
 * 创建人 kelin
 * 创建时间 2017/2/16  下午6:48
 * 版本 v 1.0.0
 */

interface LocationSearchablePage : SearchablePage {

    /**
     * 当地区按钮被点击。
     */
    fun onLocationClick()
}
