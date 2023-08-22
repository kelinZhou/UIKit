package com.kelin.uikit.delegate

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.viewpager.widget.ViewPager
import com.kelin.uikit.R
import com.kelin.uikit.flyweight.adapter.CommonFragmentStatePagerAdapter
import com.kelin.uikit.presenter.ViewPresenter
import com.kelin.uikit.widget.tablayout.PageNavigatorAdapter
import com.kelin.uikit.widget.tablayout.helper.ViewPagerTabLayoutHelper
import kotlinx.android.synthetic.main.fragment_base_view_pager_wrapper.*


/**
 * **描述:** ViewPage页面的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-08-16 13:11:03
 *
 * **版本:** v 1.0.0
 */
abstract class BaseViewPagerWrapperDelegate<VC : BaseViewPagerWrapperDelegate.BaseViewPagerWrapperDelegateCallback> : BaseViewDelegate<VC>() {

    override val rootLayoutId = R.layout.fragment_base_view_pager_wrapper

    private var lastPageNumber = 0

    override fun bindView(viewPresenter: ViewPresenter<VC>, savedInstanceState: Bundle?) {
        super.bindView(viewPresenter, savedInstanceState)
        vpViewPage.run {
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    if (position != lastPageNumber) {
                        lastPageNumber = position
                        viewCallback.onPagerNumberChanged(position)
                    }
                }
            })
            adapter = viewCallback.onCreateViewPagerAdapter()
            ViewPagerTabLayoutHelper.formatAndAttachToViewPager(
                miIndicator,
                this,
                getColor(getDefaultNormalTextColor()),
                getColor(getDefaultSelectedTextColor()),
                getColor(getDefaultIndicatorColor()),
                isTabFillParent,
                getDefaultTitleViewCreator(),
                indicatorPadding
            )
        }
    }

    protected open val indicatorPadding: Int = 0

    protected open val isTabFillParent: Boolean = true

    @ColorRes
    protected open fun getDefaultNormalTextColor(): Int = R.color.common_text_black

    @ColorRes
    protected open fun getDefaultSelectedTextColor(): Int = R.color.common_text_black

    @ColorRes
    protected open fun getDefaultIndicatorColor(): Int = R.color.theme_color

    protected open fun getDefaultTitleViewCreator(): PageNavigatorAdapter.TitleViewCreator? = null


    interface BaseViewPagerWrapperDelegateCallback : BaseViewDelegateCallback {
        fun onCreateViewPagerAdapter(): CommonFragmentStatePagerAdapter
        fun onPagerNumberChanged(position: Int)
    }
}
