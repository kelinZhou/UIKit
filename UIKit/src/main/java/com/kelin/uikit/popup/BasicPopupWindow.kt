package com.kelin.uikit.popup

import android.content.Context
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.kelin.uikit.R
import kotlinx.android.extensions.LayoutContainer

import razerdp.basepopup.BasePopupWindow

/**
 * **描述: ** PopupWindow的基类。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2018/7/30  下午6:46
 *
 * **版本: ** v 1.0.0
 */
abstract class BasicPopupWindow(context: Context, w: Int = 0, h: Int = 0) : BasePopupWindow(context, w, h), LayoutContainer {

    private var dismissListener: (() -> Unit)? = null

    final override val containerView: View?
        get() = contentView

    @LayoutRes
    protected abstract fun getRootLayoutResId(): Int

    @IdRes
    protected abstract fun getAnimViewId(): Int

    protected open fun getClickToDismissViewId(): Int = View.NO_ID

    @AnimRes
    protected open fun getShowAnimationResId(): Int = R.anim.anim_popup_top_in

    @AnimRes
    protected open fun getExitAnimationResId(): Int = R.anim.anim_popup_top_out

    fun setOnStartDismissAnimListener(listener: () -> Unit) {
        dismissListener = listener
    }

    override fun onBeforeDismiss(): Boolean {
        dismissListener?.invoke()
        return super.onBeforeDismiss()
    }

    override fun onCreateContentView(): View = createPopupById(getRootLayoutResId())

    override fun showPopupWindow() {
        onShow()
        super.showPopupWindow()
    }

    override fun showPopupWindow(v: View?) {
        onShow()
        super.showPopupWindow(v)
    }

    override fun showPopupWindow(anchorViewResid: Int) {
        onShow()
        super.showPopupWindow(anchorViewResid)
    }

    protected open fun onShow() {
        isAllowDismissWhenTouchOutside = true
    }

    protected fun <T : View> getView(id: Int): T {
        return findViewById(id)
    }
}
