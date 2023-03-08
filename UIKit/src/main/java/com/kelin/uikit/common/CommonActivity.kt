package com.kelin.uikit.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.kelin.okpermission.OkActivityResult
import com.kelin.uikit.*
import com.kelin.uikit.core.SystemError

/**
 * **描述:** 通用的Activity。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-07-30 11:58
 *
 * **版本:** v 1.0.0
 */
class CommonActivity : BaseFragmentActivity() {

    companion object {

        private const val KEY_IMMERSION_MODE = "key_immersion_mode"
        private const val KEY_NAVIGATION_ICON = "key_navigation_icon"


        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         */
        inline fun <reified F : Fragment> launch(context: Context, @StringRes title: Int) {
            launch(context, F::class.java, getString(title)) {}
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         */
        fun launch(context: Context, target: Class<out Fragment>, @StringRes title: Int) {
            launch(context, target, getString(title)) {}
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun <reified F : Fragment> launch(context: Context, @StringRes title: Int, configurator: InnerIntent.() -> Unit) {
            launch(context, F::class.java, getString(title), configurator)
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launch(context: Context, target: Class<out Fragment>, @StringRes title: Int, configurator: InnerIntent.() -> Unit) {
            launch(context, target, getString(title), configurator)
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。
         * @param title 页面的标题。
         */
        inline fun <reified F : Fragment> launch(context: Context, title: CharSequence? = null) {
            launch(context, F::class.java, title)
        }

        /**
         * 启动页面。
         * @param title 页面的标题。
         */
        fun launch(context: Context, target: Class<out Fragment>, title: CharSequence? = null) {
            launch(context, target, title) {}
        }

        /**
         * 启动页面，通过泛型指定要启动的页面。。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun <reified F : Fragment> launch(context: Context, title: CharSequence? = null, configurator: InnerIntent.() -> Unit) {
            launch(context, F::class.java, title, configurator)
        }

        /**
         * 启动页面。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launch(context: Context, target: Class<out Fragment>, title: CharSequence? = null, configurator: InnerIntent.() -> Unit) {
            InnerIntent(context, CommonActivity::class.java).apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                setTarget(target)
                title?.also { title(title) }
                configurator(this)
                context.startActivity(this, launchOptions)
            }
        }

        /**
         * 通过配置启动页面，改方法不会自动拉起Activity，需要调用者在configurator中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun <reified F : Fragment> launchByOption(context: Context, @StringRes title: Int, configurator: InnerIntent.() -> Unit) {
            launchByOption(context, F::class.java, getString(title), configurator)
        }

        /**
         * 通过配置启动页面，改方法不会自动拉起Activity，需要调用者在configurator中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launchByOption(context: Context, target: Class<out Fragment>, @StringRes title: Int, configurator: InnerIntent.() -> Unit) {
            launchByOption(context, target, getString(title), configurator)
        }

        /**
         * 通过配置启动页面，改方法不会自动拉起Activity，需要调用者在configurator中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun <reified F : Fragment> launchByOption(context: Context, title: CharSequence? = null, configurator: InnerIntent.() -> Unit) {
            launchByOption(context, F::class.java, title, configurator)
        }

        /**
         * 通过配置启动页面，改方法不会自动拉起Activity，需要调用者在configurator中手动调用start(onResult)方法拉起Activity，通常用于获取目标页面的返回值。
         * @param target 目标Fragment：本次要启动的Fragment。
         * @param title 页面的标题。
         * @param configurator 页面的配置函数，用于在启动页面前对目标页面传参。
         */
        inline fun launchByOption(context: Context, target: Class<out Fragment>, title: CharSequence? = null, configurator: InnerIntent.() -> Unit) {
            InnerIntent(context, CommonActivity::class.java).apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                title?.also { title(title) }
                setTarget(target)
                configurator(this)
            }
        }
    }


    override fun getCurrentFragment(intent: Intent): Fragment {
        return (intent as? FragmentProvider)?.provideFragment ?: onJumpError(SystemError.TARGET_PAGE_TYPE_NOT_HANDLER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (getView<View>(R.id.fragment_container)?.layoutParams as? ConstraintLayout.LayoutParams)?.also { lp ->
            ((intent.getSerializableExtra(KEY_IMMERSION_MODE) as? ImmersionMode) ?: ImmersionMode.NONE).also { mode ->
                if (mode == ImmersionMode.NONE) {
                    lp.topToBottom = R.id.vUiKitToolbarLine
                    lp.topToTop = ConstraintLayout.LayoutParams.UNSET
                } else {
                    processStatusBar(Color.TRANSPARENT)
                    if (mode == ImmersionMode.NO_TOOLBAR) {
                        supportActionBar?.hide()
                    } else {
                        intent.getIntExtra(KEY_NAVIGATION_ICON, -1).takeIf { it > 0 }?.also {
                            setNavigationIcon(it)
                        }
                    }
                    lp.topToBottom = ConstraintLayout.LayoutParams.UNSET
                    lp.topToTop = 0
                }
            }
        }
    }

    class InnerIntent(context: Context, cls: Class<*>) : Intent(context, cls), FragmentProvider {
        private var context: Context? = context

        var launchOptions: Bundle? = null
            private set

        private val isFromActivityContext: Boolean
            get() = context is Activity

        private val useActivity: Activity
            get() = useContext as Activity

        private val useContext: Context
            get() {
                val contextTemp = context
                context = null
                return contextTemp ?: throw NullPointerException("The context used early!")
            }

        fun setTarget(target: Class<out Fragment>) {
            putExtra("key_target_fragment_class", target)
        }

        @Suppress("UNCHECKED_CAST")
        override val provideFragment: Fragment?
            get() = (getSerializableExtra("key_target_fragment_class") as? Class<out Fragment>)?.let {
                BasicFragment.newInstance(it, extras)
            }

        /**
         * 为新的页面设置标题，仅在没有调用immersion或immersionToolbar方法时生效。
         * @param title 页面的标题。
         */
        fun title(title: CharSequence) {
            putExtra("key_page_title", title)
        }

        /**
         * 为新的页面设置标题，仅在没有调用immersion或immersionToolbar方法时生效。
         * @param title 页面的标题。
         * @see immersion
         * @see immersionToolbar
         */
        fun title(@StringRes title: Int) {
            putExtra("key_page_title", getString(title))
        }

        /**
         * 为新的页面设置沉浸式样式，设置沉浸式样式后会隐藏默认的Toolbar。
         */
        fun immersion() {
            putExtra(KEY_IMMERSION_MODE, ImmersionMode.NO_TOOLBAR)
        }

        /**
         * 为新的页面设置沉浸式样式，设置沉浸式样式后Toolbar会盖在目标页面上，需要为目标页面设置一定的顶部间距保证Toolbar不会遮挡到目标页面的内容。
         */
        fun immersionToolbar(@DrawableRes navigationIcon: Int? = null) {
            putExtra(KEY_IMMERSION_MODE, ImmersionMode.HAVE_TOOLBAR)
            putExtra(KEY_NAVIGATION_ICON, navigationIcon)
        }

        /**
         * Activity的启动配置。
         * @param options 配置内容。
         */
        fun options(options: Bundle) {
            launchOptions = options
        }

        /**
         * 启动页面。
         * @param onResult 如果需要从目标页面获取结果则需要传入回调。
         */
        fun start(onResult: ((resultCode: Int) -> Unit)?) {
            (this as? InnerIntent)?.also {
                if (onResult != null && it.isFromActivityContext) {
                    OkActivityResult.startActivity(it.useActivity, this, launchOptions, onResult)
                } else {
                    it.useContext.startActivity(this, launchOptions)
                }
            }
        }

        /**
         * 启动页面。
         * @param onResult 如果需要从目标页面获取结果则需要传入回调。
         */
        fun <D> start(onResult: ((resultCode: Int, data: D?) -> Unit)?) {
            (this as? InnerIntent)?.also {
                if (onResult != null && it.isFromActivityContext) {
                    OkActivityResult.startActivity(it.useActivity, this, launchOptions, onResult)
                } else {
                    it.useContext.startActivity(this, launchOptions)
                }
            }
        }
    }

    /**
     * 沉浸模式。
     */
    private enum class ImmersionMode {
        /**
         * 不开启。
         */
        NONE,

        /**
         * 没有Toolbar的。
         */
        NO_TOOLBAR,

        /**
         * 有Toolbar的。
         */
        HAVE_TOOLBAR
    }
}