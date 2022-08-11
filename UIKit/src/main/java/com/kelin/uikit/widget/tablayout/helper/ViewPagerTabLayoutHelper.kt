package com.kelin.uikit.widget.tablayout.helper

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.kelin.uikit.R
import com.kelin.uikit.widget.tablayout.PageNavigatorAdapter
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 * **描述:** 第三方TabLayout的帮助类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/8/16 7:15 PM
 *
 * **版本:** v 1.0.0
 */
object ViewPagerTabLayoutHelper {
    @SuppressLint("InflateParams")
    fun createCommonToolbarTabLayout(viewPager: ViewPager): MagicIndicator {
        val indicator = MagicIndicator(viewPager.context)
        formatAndAttachToViewPager(indicator, viewPager, Color.parseColor("#333333"), Color.parseColor("#333333"), ContextCompat.getColor(viewPager.context, R.color.colorAccent), false, null)
        return indicator
    }

    fun formatAndAttachToViewPager(
        indicator: MagicIndicator,
        viewPager: ViewPager,
        normalTextColor: Int = ContextCompat.getColor(viewPager.context, R.color.common_text_black),
        selectedTextColor: Int = ContextCompat.getColor(viewPager.context, R.color.common_text_black),
        indicatorColor: Int = ContextCompat.getColor(viewPager.context, R.color.colorAccent),
        isFillParent: Boolean = true,
        titleViewCreator: PageNavigatorAdapter.TitleViewCreator? = null,
        padding: Int = 6,
        touchable: Boolean = true
    ) {
        val commonNavigator = CommonNavigator(viewPager.context)
        commonNavigator.isAdjustMode = isFillParent
        commonNavigator.adapter =
            PageNavigatorAdapter(touchable, viewPager, normalTextColor, selectedTextColor, indicatorColor, LinePagerIndicator.MODE_EXACTLY, isFillParent, titleViewCreator, padding)
        indicator.navigator = commonNavigator
        ViewPagerHelper.bind(indicator, viewPager)
    }
}