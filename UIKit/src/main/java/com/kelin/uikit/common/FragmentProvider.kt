package com.kelin.uikit.common

import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * Fragment提供者。
 */
interface FragmentProvider : Serializable {
    val provideFragment: Fragment?
}