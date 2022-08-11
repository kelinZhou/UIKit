package com.kelin.uikit.widget.tablayout;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import org.jetbrains.annotations.NotNull;

/**
 * **描述:** ViewPager指示器的适配器。
 * <p>
 * **创建人:** kelin
 * <p>
 * **创建时间:** 2021/8/16 7:15 PM
 * <p>
 * **版本:** v 1.0.0
 */
public class PageNavigatorAdapter extends CommonNavigatorAdapter implements ViewPager.OnAdapterChangeListener {
    private final ViewPager viewPager;
    private PagerAdapter adapter;
    @ColorInt
    private int normalTextColor;
    @ColorInt
    private int selectedTextColor;
    @ColorInt
    private int indicatorColor;
    private int indicatorMode;
    private boolean isFillParent;
    private TitleViewCreator titleViewCreator;
    private int mPadding;
    private boolean touchable;

    public PageNavigatorAdapter(Boolean touchable, ViewPager viewPager, int normalTextColor, int selectedTextColor, int indicatorColor, int indicatorMode, boolean isFillParent, TitleViewCreator titleViewCreator, int padding) {
        this.touchable = touchable;
        this.normalTextColor = normalTextColor;
        this.selectedTextColor = selectedTextColor;
        this.indicatorColor = indicatorColor;
        this.indicatorMode = indicatorMode;
        this.viewPager = viewPager;
        this.adapter = viewPager.getAdapter();
        this.isFillParent = isFillParent;
        this.titleViewCreator = titleViewCreator;
        this.mPadding = padding;
        if (adapter == null) {
            viewPager.addOnAdapterChangeListener(this);
        }
    }

    @Override
    public int getCount() {
        return adapter != null ? adapter.getCount() : 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, int index) {
        return onCreateTitleView(context, index);
    }

    @NotNull
    private IPagerTitleView onCreateTitleView(Context context, int index) {
        if (titleViewCreator == null) {
            CommonToolbarTabTitleView titleView = new CommonToolbarTabTitleView(context, null);
            titleView.setTextSize(17);
            titleView.setNormalColor(normalTextColor);
            titleView.setSelectedColor(selectedTextColor);
            titleView.setText(adapter.getPageTitle(index));
            if (touchable) {
                titleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
            }
            if (isFillParent) {
                if (index == 0) {
                    titleView.setPadding(dp2px(context, 6) + titleView.getPaddingLeft(), titleView.getPaddingTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
                } else if (index == getCount() - 1) {
                    titleView.setPadding(titleView.getPaddingLeft(), titleView.getPaddingTop(), dp2px(context, 6) + titleView.getPaddingRight(), titleView.getPaddingBottom());
                }
            } else {
                if (index == 0) {
                    titleView.setPadding(dp2px(context, 6) + titleView.getPaddingLeft(), titleView.getPaddingTop(), dp2px(context, mPadding) + titleView.getPaddingRight(), titleView.getPaddingBottom());
                } else if (index == getCount() - 1) {
                    titleView.setPadding(dp2px(context, mPadding) + titleView.getPaddingLeft(), titleView.getPaddingTop(), dp2px(context, 6) + titleView.getPaddingRight(), titleView.getPaddingBottom());
                }
            }
            return titleView;
        } else {
            return titleViewCreator.createTitleView(context, index);
        }
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setLineHeight(dp2px(context, 4));
        indicator.setMode(indicatorMode);
        indicator.setColors(indicatorColor);
        indicator.setRoundRadius(10);
        indicator.setLineWidth(30);
        indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
        return indicator;
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
        this.adapter = newAdapter;
        if (newAdapter != null) {
            listenerDataSetChanged();
        }
    }

    private void listenerDataSetChanged() {
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                notifyDataSetChanged();
            }
        });
    }


    public interface TitleViewCreator {
        IPagerTitleView createTitleView(Context context, int index);
    }

    private int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
