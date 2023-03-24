package com.kelin.uikitdemo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.collection.ArraySet
import com.kelin.uikit.BasicFragment
import com.kelin.uikit.common.search.SearchableHistoryPage
import com.kelin.uikit.common.search.SearchablePage
import com.kelin.uikit.common.search.Searcher
import com.kelin.uikit.setMarginTop
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
class TestHistoryFragment : BasicFragment(), SearchableHistoryPage {

    private val history by lazy { ArrayList<String>() }

    private val contentView: TextView by lazy {
        TextView(context).apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
            gravity = Gravity.CENTER
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setBackgroundColor(Color.rgb(Random.nextDouble(1.0).toFloat(), Random.nextDouble(1.0).toFloat(), Random.nextDouble(1.0).toFloat()))
            }
        }
    }

//    override fun onImmersion(offset: Int) {
//        contentView.setMarginTop(offset)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return contentView
    }

    override fun onInitSearchPage(searcher: Searcher) {
        contentView.text = "初始化搜索"
    }

    @SuppressLint("SetTextI18n")
    override fun onSearch(searchKey: String) {
        if (!history.contains(searchKey)) {
            history.add(searchKey)
        }
    }

    override fun onRealResume() {
        super.onRealResume()
        contentView.text = history.joinToString(" | ").let {
            if (it.isBlank()) {
                "我是搜索历史页面"
            } else {
                "历史：${it}"
            }
        }
    }

    override fun onSearchCancel() {
        contentView.text = "取消搜索"
    }
}