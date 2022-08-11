package com.kelin.uikit.widget.optionsmenu.exception

import java.lang.RuntimeException

/**
 * **描述:** 非法调用异常。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/2/21  11:35 AM
 *
 * **版本:** v 1.0.0
 */
class IllegalCalledException(message: String = "") : RuntimeException(message)