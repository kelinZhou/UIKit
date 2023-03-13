package com.kelin.uikit.common

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kelin.uikit.BasicFragment
import kotlin.random.Random

/**
 * **描述:** 占位页面
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2022/12/29 3:09 PM
 *
 * **版本:** v 1.0.0
 */
class PlaceholderFragment : BasicFragment() {

    companion object {

        private const val KEY_PLACEHOLDER_NAME = "key_placeholder_name"

        fun createInstance(name: String): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_PLACEHOLDER_NAME, name)
                }
            }
        }

        fun setName(intent: Intent, name: String) {
            intent.putExtra(KEY_PLACEHOLDER_NAME, name)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return TextView(context).apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
            gravity = Gravity.CENTER
            text = arguments?.getString(KEY_PLACEHOLDER_NAME, "我是占位页面") ?: "我是占位页面"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setBackgroundColor(Color.rgb(Random.nextDouble(1.0).toFloat(), Random.nextDouble(1.0).toFloat(), Random.nextDouble(1.0).toFloat()))
            }
        }
    }
}