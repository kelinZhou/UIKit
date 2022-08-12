package com.kelin.uikit.search

/**
 * **描述:** 支持联想词的搜索页面。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-09-30  13:54
 *
 * **版本:** v 1.0.0
 */
interface AssociateSearchablePage : LocationSearchablePage {

    /**
     * 获取联想词。
     * @param keyword 当前的关键字。
     */
    fun getAutomaticAssociations(keyword: String)
}