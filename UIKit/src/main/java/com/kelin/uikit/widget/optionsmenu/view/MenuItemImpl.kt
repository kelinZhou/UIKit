package com.kelin.uikit.widget.optionsmenu.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.*
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kelin.uikit.R
import com.kelin.uikit.setDrawableStart
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_custom_popup_menu_item.*

/**
 * **描述:** MenuItem菜单的实现类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/2/21  11:30 AM
 *
 * **版本:** v 1.0.0
 */
class MenuItemImpl(private val menuId: Int, val context: Context) : MenuItem, LayoutContainer {
    override val containerView: View
        get() = itemView

    /**
     * 用来记录当前menuItem是否可用。
     */
    private var isEnabled = true

    /**
     * 用来记录当前menuItem是否可以被选中。
     */
    private var isCheckable = false

    /**
     * 用来记录当前menuItem是否已经被选中。
     */
    private var isChecked = false

    /**
     * 用来记录当前menuItem的Intent。
     */
    private var menuIntent: Intent? = null

    /**
     * 用来记录当前menuItem的icon。
     */
    private var menuIcon: Drawable? = null
        set(value) {
            itemView.setDrawableStart(value)
            field = value
        }

    /**
     * 当前menuItem菜单的View。
     */
    private lateinit var itemView: TextView

    init {
        createView()
    }

    @SuppressLint("InflateParams")
    private fun createView() {
        itemView = LayoutInflater.from(context).inflate(R.layout.layout_custom_popup_menu_item, null) as TextView
    }

    override fun getItemId(): Int {
        return menuId
    }

    override fun setEnabled(enabled: Boolean): MenuItem {
        isEnabled = enabled
        return this
    }

    override fun isEnabled() = isEnabled

    override fun setTitle(title: CharSequence?): MenuItem {
        tvMenuTitle.text = title
        return this
    }

    override fun setTitle(@StringRes title: Int): MenuItem {
        tvMenuTitle.setText(title)
        return this
    }

    override fun getTitle(): CharSequence? = tvMenuTitle.text

    override fun setCheckable(checkable: Boolean): MenuItem {
        isCheckable = checkable
        return this
    }

    override fun isCheckable() = isCheckable

    override fun setChecked(checked: Boolean): MenuItem {
        isChecked = checked
        return this
    }

    override fun isChecked() = isChecked

    override fun setIntent(intent: Intent?): MenuItem {
        menuIntent = intent
        return this
    }

    override fun getIntent(): Intent? = menuIntent

    override fun setVisible(visible: Boolean): MenuItem {
        itemView.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    override fun isVisible() = itemView.visibility == View.VISIBLE

    override fun setIcon(icon: Drawable?): MenuItem {
        menuIcon = icon
        return this
    }

    @SuppressLint("ResourceType", "UseCompatLoadingForDrawables")
    override fun setIcon(@DrawableRes iconRes: Int): MenuItem {
        menuIcon = when {
            iconRes <= 0 -> null
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> context.getDrawable(iconRes)
            else -> context.resources.getDrawable(iconRes)
        }
        return this
    }

    override fun getIcon(): Drawable? = menuIcon

    override fun setOnMenuItemClickListener(itemClickListener: MenuItem.OnMenuItemClickListener?): MenuItem {
        itemView.setOnClickListener { itemClickListener?.onMenuItemClick(this) }
        return this
    }

    override fun expandActionView(): Boolean = false

    override fun hasSubMenu(): Boolean = false  //暂时不支持subMenu

    override fun getMenuInfo(): ContextMenu.ContextMenuInfo? = null

    override fun getAlphabeticShortcut(): Char = ' '

    override fun getActionView(): View? = null

    override fun getOrder(): Int = 0

    override fun setOnActionExpandListener(listener: MenuItem.OnActionExpandListener?): MenuItem {
        return this
    }

    override fun setShowAsAction(actionEnum: Int) {

    }

    override fun getGroupId(): Int = 0

    override fun setActionProvider(actionProvider: ActionProvider?): MenuItem {
        return this
    }

    override fun setTitleCondensed(title: CharSequence?): MenuItem {
        return this
    }

    override fun getNumericShortcut(): Char = ' '

    override fun isActionViewExpanded(): Boolean = false

    override fun collapseActionView(): Boolean = false

    override fun setNumericShortcut(numericChar: Char): MenuItem {
        return this
    }

    override fun setActionView(view: View?): MenuItem {
        return this
    }

    override fun setActionView(resId: Int): MenuItem {
        return this
    }

    override fun setAlphabeticShortcut(alphaChar: Char): MenuItem {
        return this
    }

    override fun setShortcut(numericChar: Char, alphaChar: Char): MenuItem {
        return this
    }

    override fun setShowAsActionFlags(actionEnum: Int): MenuItem {
        return this
    }

    override fun getActionProvider(): ActionProvider? = null

    override fun getSubMenu(): SubMenu? = null

    override fun getTitleCondensed(): CharSequence = ""
}