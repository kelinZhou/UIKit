package com.kelin.uikitdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kelin.apiexception.ApiException
import com.kelin.photoselector.PhotoSelector
import com.kelin.proxyfactory.Toaster
import com.kelin.uikit.UIKit
import com.kelin.uikit.common.*
import com.kelin.uikit.tools.statusbar.StatusBarHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarHelper.setStatusBarLightMode(this)
        UIKit.init(application, MyToaster(), true)
        PhotoSelector.init(this, "${packageName}.fileProvider", true, albumTakePictureEnable = false)
        setContentView(R.layout.activity_main)
        btnCommonDemo.setOnClickListener {
            Navigation.launch<PlaceholderFragment>(this, "新的页面")
        }

        btnCommonDemo2.setOnClickListener {
            Navigation.launch<PlaceholderFragment>(this, "新的页面") {
                immersionToolbar()  //设置沉浸式Toolbar。
                PlaceholderFragment.setName(intent, "新的页面")
                results<String> {
                    if (it != null) {
                        //do something!
                    }
                }
            }
        }

        btnTabDemo.setOnClickListener {
            Navigation.launchTabOnly(this) {
                page("生活", PlaceholderFragment::class)
                page("汽车", PlaceholderFragment.createInstance("汽车"))
                page("圈子", PlaceholderFragment.createInstance("圈子"))
            }
        }

        btnTabDemo2.setOnClickListener {
            Navigation.launchTab(this) {
                immersion()
                defaultIndex(1)
                pages {
                    PlaceholderFragment::class to "生活"
                    PlaceholderFragment.createInstance("汽车") to "汽车"
                    PlaceholderFragment.createInstance("圈子") to "圈子"
                }
            }
        }
        btnTabDemo3.setOnClickListener {
            Navigation.launchTab(this) {
                defaultIndex(1)
                pages {
                    BannerShowDemoFragment::class to "轮播图"
                    PlaceholderFragment.createInstance("汽车") to "汽车"
                    PlaceholderFragment.createInstance("圈子") to "圈子"
                }
            }
        }
        btnTabDemo4.setOnClickListener {
            Navigation.launchSearch<TestSearchFragment>(this)
        }
        btnTabDemo5.setOnClickListener {
            Navigation.launchSearch<TestSearchFragment>(this) {
                immersion()
                historyPage(TestHistoryFragment::class.java)
            }
        }
        btnTabDemo6.setOnClickListener {
            Navigation.launchH5(this, "https://www.baidu.com", "百度") {
                byBrowser()
            }
        }
        btnTabDemo7.setOnClickListener {
            Navigation.launchH5(
                this,
                "http://rna.lenovoconnect.com/home",
                "懂得物联"
            )
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