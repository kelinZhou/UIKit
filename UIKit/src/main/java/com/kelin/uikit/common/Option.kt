package com.kelin.uikit.common

/**
 * **描述:** 普通页面的配置项。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/8/22 9:55 AM
 *
 * **版本:** v 1.0.0
 */
interface Option : IOption {
    /**
     * 为新的页面设置标题是否居中显示，仅在没有调用immersion或immersionToolbar方法时生效。
     * @param center 标题是否居中显示，没有设置时默认为居中显示。
     * @see immersion
     * @see immersionToolbar
     */
    fun titleCenter(center: Boolean)
}