package com.kelin.uikit.common

import android.content.Intent
import androidx.fragment.app.Fragment
import com.kelin.uikit.BasicFragment

/**
 * Fragment提供者。
 */
object FragmentProvider {

    fun setTargetFragment(intent: Intent, target: Class<out Fragment>) {
        intent.putExtra("key_target_fragment_class", target)
    }

    @Suppress("UNCHECKED_CAST")
    fun provideFragment(intent: Intent): Fragment? {
        return (intent.getSerializableExtra("key_target_fragment_class") as? Class<out Fragment>)?.let {
            BasicFragment.newInstance(it, intent.extras)
        }
    }
}