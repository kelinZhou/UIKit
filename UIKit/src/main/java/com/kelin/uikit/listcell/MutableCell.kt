package com.kelin.uikit.listcell


/**
 * **描述:** 列表中可以根据位置设置不同样式的Cell。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/4/24 9:41 PM
 *
 * **版本:** v 1.0.0
 */
abstract class MutableCell(val isLast: Boolean, val isFirst: Boolean = false) : SimpleCell()