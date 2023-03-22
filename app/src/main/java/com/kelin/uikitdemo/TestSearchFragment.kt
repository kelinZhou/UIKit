package com.kelin.uikitdemo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kelin.uikit.BasicFragment
import com.kelin.uikit.common.search.SearchablePage
import com.kelin.uikit.common.search.Searcher
import kotlin.random.Random

/**
 * **描述:** 占位页面
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2022/12/29 3:09 PM
 *
 * **版本:** v 1.0.0
 */
class TestSearchFragment : BasicFragment(), SearchablePage {

    private val contentView: TextView by lazy {
        TextView(context).apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
            gravity = Gravity.CENTER
            text = "我是搜索页面"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setBackgroundColor(Color.rgb(Random.nextDouble(1.0).toFloat(), Random.nextDouble(1.0).toFloat(), Random.nextDouble(1.0).toFloat()))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return contentView
    }

    override val searchHint: String
        get() = "测试搜索"

    override fun onInitSearchPage(searcher: Searcher) {
        contentView.text = "初始化搜索"
    }

    @SuppressLint("SetTextI18n")
    override fun onSearch(searchKey: String) {
        contentView.text = if (searchKey.isBlank()) "我是搜索页面" else "搜索：${searchKey}"
    }

    override fun onSearchCancel() {
        contentView.text = "取消搜索"
    }
}