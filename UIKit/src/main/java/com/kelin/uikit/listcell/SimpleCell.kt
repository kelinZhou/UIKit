package com.kelin.uikit.listcell

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.annotation.*
import com.kelin.uikit.R

import java.lang.Exception

/**
 * **描述: ** 简单的默认实现的Cell，在通常情况下继承自该类可以更加快速的进行开发。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2020/3/10  下午12:49
 *
 * **版本: ** v 1.0.0
 */
abstract class SimpleCell : Cell {

    private var owner: CellOwner? = null

    var isInWindow = false
        private set

    override val layoutPosition: Int
        get() = owner?.cellLayoutPosition ?: 0

    override val containerView: View?
        get() = owner?.cellItemView

    protected val resources: Resources
        get() = containerView!!.resources

    override val spanSize: Int
        get() = Int.MAX_VALUE

    protected val context: Context by lazy { containerView!!.context }

    @Suppress("UNCHECKED_CAST")
    fun <T> getItemData(): T? {
        return owner?.layoutItemData() as? T
    }

    @CallSuper
    final override fun onCreate(owner: CellOwner) {
        this.owner = owner
        onCreate(owner.cellItemView)
    }

    final override fun setIsRecyclable(recyclable: Boolean) {
        owner?.setIsRecyclable(recyclable)
    }

    open fun onCreate(itemView: View) {
    }

    final override fun bindData(owner: CellOwner) {
        this.owner = owner
        onBindData(owner.cellItemView)
    }

    protected abstract fun onBindData(iv: View)

    final override fun onItemClick(context: Context, position: Int) = onItemClick(getItemView(), context, position)

    protected open fun onItemClick(iv: View, context: Context, position: Int) {}

    final override fun onItemLongClick(context: Context, position: Int) = onItemLongClick(getItemView(), context, position)

    protected open fun onItemLongClick(iv: View, context: Context, position: Int) {}

    final override fun onItemChildClick(context: Context, position: Int, v: View) {
        onItemChildClick(getItemView(), context, position, v)
    }

    protected open fun onItemChildClick(iv: View, context: Context, position: Int, v: View) {}

    override val clickableIds: IntArray?
        get() = null

    override val itemClickable: Boolean
        get() = false

    override val itemLongClickable: Boolean
        get() = false

    override val haveItemClickBg: Boolean
        get() = itemClickable

    override val itemClickableViewId: Int
        get() = 0

    override val itemBackgroundResource: Int
        get() = R.drawable.selector_recycler_item_bg

    override fun needFilterDoubleClick(v: View): Boolean = true

    @CallSuper
    final override fun onViewAttachedToWindow(owner: CellOwner, position: Int) {
        this.owner = owner
        isInWindow = true
        onViewAttachedToWindow(owner.cellItemView, position)
    }

    @CallSuper
    final override fun onViewDetachedFromWindow(owner: CellOwner, position: Int) {
        this.owner = owner
        isInWindow = false
        onViewDetachedFromWindow(owner.cellItemView, position)
    }

    protected fun refreshItemBackground() {
        owner?.refreshItemBackground()
    }

    protected open fun onViewAttachedToWindow(iv: View, position: Int) {
    }

    protected open fun onViewDetachedFromWindow(iv: View, position: Int) {
    }

    @Suppress("UNCHECKED_CAST")
    fun <V : View> getItemView(): V = (owner!!.cellItemView as? V) ?: throw ClassCastException("${owner!!.cellItemView.javaClass.simpleName} can't case be V!")

    @Suppress("UNCHECKED_CAST")
    fun <V : View> getItemViewOrNull(): V? = (owner?.cellItemView as? V)

    fun <V : View> getView(@IdRes viewId: Int): V = getItemView<View>().findViewById(viewId)

    fun getDimension(@DimenRes dimen: Int): Int {
        return resources.getDimension(dimen).toInt()
    }

    protected fun getString(@StringRes stringResId: Int, vararg formatArgs: Any): String {
        return resources.let {
            if (formatArgs.isEmpty()) {
                it.getString(stringResId)
            } else {
                it.getString(stringResId, *formatArgs)
            }
        }
    }

    @ColorInt
    fun getColor(@ColorRes colorRes: Int): Int {
        return resources.getColor(colorRes)
    }

    fun getDrawable(@DrawableRes drawableRes: Int): Drawable? {
        return try {
            if (drawableRes == 0) {
                null
            } else {
                resources.getDrawable(drawableRes)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun setDrawableTop(tv: TextView, @DrawableRes drawableRes: Int) {
        val drawable = getDrawable(drawableRes)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(null, drawable, null, null)
    }

    fun setDrawableStart(tv: TextView, @DrawableRes drawableRes: Int) {
        val drawable = getDrawable(drawableRes)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(drawable, null, null, null)
    }

    fun setDrawableEnd(tv: TextView, @DrawableRes drawableRes: Int) {
        val drawable = getDrawable(drawableRes)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(null, null, drawable, null)
    }

    fun setDrawableStartAndEnd(tv: TextView, @DrawableRes drawableStart: Int, @DrawableRes drawableEnd: Int) {
        val start = getDrawable(drawableStart)
        start?.setBounds(0, 0, start.minimumWidth, start.minimumHeight)
        val end = getDrawable(drawableEnd)
        end?.setBounds(0, 0, end.minimumWidth, end.minimumHeight)
        tv.setCompoundDrawables(start, null, end, null)
    }
}
