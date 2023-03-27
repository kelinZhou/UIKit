package com.kelin.uikit.common.h5

import java.io.Serializable

/**
 * **描述:** 用来包装JavascriptInterface。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2023/3/27 11:17 AM
 *
 * **版本:** v 1.0.0
 */
internal data class JavascriptInterfaceWrapper(
    val interfaceName: String,
    val interfaceClass: Class<out JsInterface>
) : Serializable
