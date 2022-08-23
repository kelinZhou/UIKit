package com.kelin.uikit

import android.app.Application
import android.content.Context
import com.kelin.apiexception.ApiException
import com.kelin.logger.LogOption
import com.kelin.proxyfactory.ProxyFactory
import com.kelin.proxyfactory.Toaster
import java.util.*

/**
 * **描述:** UIKit的核心类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2022/8/11 5:16 PM
 *
 * **版本:** v 1.0.0
 */
object UIKit {

    internal var isDebugMode = false
    private var context: Application? = null
    internal lateinit var toaster: Toaster
    internal var defaultLocale: Locale? = null

    fun init(context: Application, toaster: Toaster, isDebugMode: Boolean, defLocale: Locales? = null) {
        this.context = context
        this.toaster = toaster
        this.isDebugMode = isDebugMode
        this.defaultLocale = defLocale?.locale
        ApiException.init(context)
        ProxyFactory.init(context, toaster)
        LogOption.init("", isDebugMode)
    }

    internal fun getContext(): Context {
        return context
            ?: throw NullPointerException("You must call the UIKit.init() Method before use the UIKit")
    }

    sealed class Locales(val locale: Locale) {
        object CHINA : Locales(Locale.CHINA)
        object US : Locales(Locale.US)
    }
}