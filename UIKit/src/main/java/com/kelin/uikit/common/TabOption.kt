package com.kelin.uikit.common

import android.content.Intent
import com.kelin.uikit.flyweight.adapter.CommonFragmentStatePagerAdapter

/**
 * **描述:** Tab页面的配置。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/21 11:34 AM
 *
 * **版本:** v 1.0.0
 */
interface TabOption : Option {

    companion object{
        internal const val KEY_INNER_INTENT_DEF_INDEX = "key_inner_intent_def_index"
        internal const val KEY_INNER_INTENT_ADAPTER_CONFIG = "key_inner_intent_adapter_config"
        internal const val KEY_INNER_INTENT_IS_FOR_TAB_MODE_SLIDE_ENABLE = "key_inner_intent_is_for_tab_mode_slide_enable"

        /**
         * 判断ViewPage的滑动翻页是否可用。
         */
        internal fun isTabSlideEnable(intent: Intent): Boolean {
            return Option.getPageMode(intent) == PageMode.TAB && intent.getBooleanExtra(KEY_INNER_INTENT_IS_FOR_TAB_MODE_SLIDE_ENABLE, false)
        }

        /**
         * 获取Tab模式下的默认页面索引。
         */
        internal fun getTabDefIndex(intent: Intent): Int {
            return intent.getIntExtra(KEY_INNER_INTENT_DEF_INDEX, 0)
        }

        /**
         * 获取Tab模式下Adapter配置。
         */
        internal fun getTabPageConfig(intent: Intent): PageConfigurationWrapper? {
            return intent.getSerializableExtra(KEY_INNER_INTENT_ADAPTER_CONFIG) as? PageConfigurationWrapper
        }
    }

    /**
     * 设置Tab模式下默认显示的页面索引。
     * @param index 默认显示的索引。
     */
    fun defaultIndex(index: Int) {
        intent.putExtra(KEY_INNER_INTENT_DEF_INDEX, index)
    }

    /**
     * 配置页面。
     * @param configuration 页面的配置回调。
     */
    fun pages(configuration: CommonFragmentStatePagerAdapter.() -> Unit) {
        intent.putExtra(KEY_INNER_INTENT_ADAPTER_CONFIG, PageConfigurationWrapper(configuration))
    }
}