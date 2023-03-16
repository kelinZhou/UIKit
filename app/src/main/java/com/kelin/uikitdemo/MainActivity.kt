package com.kelin.uikitdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kelin.apiexception.ApiException
import com.kelin.proxyfactory.Toaster
import com.kelin.uikit.UIKit
import com.kelin.uikit.common.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIKit.init(application, MyToaster(), true)
        setContentView(R.layout.activity_main)
        btnCommonDemo.setOnClickListener {
            CommonActivity.launch<PlaceholderFragment>(this, "新的页面")
        }

        btnCommonDemo2.setOnClickListener {
            CommonActivity.launch<PlaceholderFragment>(this, "新的页面") {
                immersionToolbar()  //设置沉浸式Toolbar。
                PlaceholderFragment.setName(this, "新的页面")
            }
        }

        btnTabDemo.setOnClickListener {
            CommonActivity.launchTabOnly(this) {
                "生活" to PlaceholderFragment::class
                "汽车" to PlaceholderFragment.createInstance("汽车")
                "圈子" to PlaceholderFragment.createInstance("圈子")
            }
        }

        btnTabDemo2.setOnClickListener {
            CommonActivity.launchTabOnly(this, immersion = true) {
                "汽车" to PlaceholderFragment.createInstance("汽车")
                "生活" to PlaceholderFragment::class
                "圈子" to PlaceholderFragment.createInstance("圈子")
            }
        }
    }

    private class MyToaster : Toaster {
        override fun handError(e: Throwable): ApiException? {
            return e as? ApiException
        }

        override fun hideProgress(context: Context) {
        }

        override fun showFailedToast(e: ApiException) {
        }

        override fun showProgress(context: Context, progressTip: String?) {
        }
    }
}