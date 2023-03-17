package com.kelin.uikitdemo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kelin.banner.SimpleBannerEntry
import com.kelin.uikit.BasicFragment
import com.kelin.uikit.tools.ToastUtil
import kotlinx.android.synthetic.main.fragment_banner_show_more.*
import java.util.*
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
class BannerShowDemoFragment : BasicFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_banner_show_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bvPlacardBanner.setEntries(
            listOf(
                TestEntry(1),
                TestEntry(2),
                TestEntry(3)
            ), false
        )
        ssmSlideShowMore.setOnShowMoreListener {
            ToastUtil.showShortToast("显示更多！")
        }
    }

    class TestEntry(private val index: Int) : SimpleBannerEntry<Int>() {
        override fun onCreateView(parent: ViewGroup): View {
            return TextView(parent.context).apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
                gravity = Gravity.CENTER
                text = index.toString()
                textSize = 30F
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    setBackgroundColor(Color.rgb(Random.nextDouble(1.0).toFloat(), Random.nextDouble(1.0).toFloat(), Random.nextDouble(1.0).toFloat()))
                }
            }
        }

        override fun getImageUrl(): String {
            return UUID.randomUUID().toString()
        }
    }
}