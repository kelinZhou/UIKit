package com.kelin.uikit.common

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.kelin.uikit.common.search.SearchableHistoryPage
import com.kelin.uikit.common.search.SearchablePage


/**
 * **描述:** Tab页面的配置。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/21 11:34 AM
 *
 * **版本:** v 1.0.0
 */
interface SearchOption : Option {

    companion object {
        private const val KEY_INITIAL_SEARCH = "key_initial_search"
        private const val KEY_INSTANT_SEARCH = "key_instant_search"
        private const val KEY_SEARCH_HISTORY_TARGET_PAGE = "key_search_history_target_page"
        private const val KEY_SOFT_INPUT_MODE = "key_soft_input_mode"

        @Suppress("UNCHECKED_CAST")
        internal fun getHistoryPage(intent: Intent): SearchableHistoryPage? {
            return (intent.getSerializableExtra(KEY_SEARCH_HISTORY_TARGET_PAGE) as? Class<out SearchableHistoryPage>)?.let { newInstance(it, intent.extras) }
        }

        internal fun getSoftInputMode(intent: Intent): Int {
            return intent.getIntExtra(KEY_SOFT_INPUT_MODE, WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        internal fun isInitialSearch(intent: Intent): Boolean {
            return intent.getBooleanExtra(KEY_INITIAL_SEARCH, intent.getSerializableExtra(KEY_SEARCH_HISTORY_TARGET_PAGE) == null)
        }

        internal fun isInstantSearch(intent: Intent): Boolean {
            return intent.getBooleanExtra(KEY_INSTANT_SEARCH, intent.getSerializableExtra(KEY_SEARCH_HISTORY_TARGET_PAGE) == null)
        }

        private fun <P : SearchableHistoryPage> newInstance(cls: Class<out P>, extras: Bundle?): P {
            return try {
                cls.newInstance().also {
                    if (it !is Fragment) {
                        throw ClassCastException("the SearchablePage:" + cls.simpleName + " must be a Fragment!")
                    }
                    (it as? Fragment)?.arguments = extras
                }
            } catch (e: Exception) {
                throw RuntimeException("实例化失败:" + e.message, e)
            }
        }
    }

    /**
     * 启动搜索页面后是否直接将搜索结果页面加载。
     * @param enable  true表示直接出发搜索，false表示不直接出发搜索，默认为true。
     */
    fun initialSearch(enable: Boolean) {
        intent.putExtra(KEY_INITIAL_SEARCH, enable && intent.getSerializableExtra(KEY_SEARCH_HISTORY_TARGET_PAGE) == null)
    }

    /**
     * 是否开启实时搜索，即用户每输入一个字符就触发一次搜索。
     * @param enable  true表示开启，false表示关闭，默认为false。
     */
    fun instantSearch(enable: Boolean) {
        //如果没有搜索历史才可以开启实时搜索：intent.getSerializableExtra(KEY_SEARCH_HISTORY_TARGET_PAGE) == null。
        intent.putExtra(KEY_INSTANT_SEARCH, enable && intent.getSerializableExtra(KEY_SEARCH_HISTORY_TARGET_PAGE) == null)
    }

    /**
     * 设置搜索历史页面。
     * @param cls 搜索历史页面。
     */
    fun historyPage(cls: Class<out SearchableHistoryPage>) {
        intent.putExtra(KEY_SEARCH_HISTORY_TARGET_PAGE, cls)
        initialSearch(false)
        instantSearch(false)
    }

    /**
     * 设置键盘输入模式。
     */
    fun setSoftInputMode(inputMode: Int) {
        intent.putExtra(KEY_SOFT_INPUT_MODE, inputMode)
    }
}