package com.kelin.uikitdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kelin.apiexception.ApiException
import com.kelin.proxyfactory.Toaster
import com.kelin.uikit.UIKit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIKit.init(application, MyToaster(), true)
        setContentView(R.layout.activity_main)
    }

    private class MyToaster :Toaster {
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