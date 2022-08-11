package com.kelin.uikit.widget.optionsmenu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import android.util.SparseArray
import android.util.Xml
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import com.kelin.uikit.widget.optionsmenu.exception.IllegalTagException
import com.kelin.uikit.R
import com.kelin.uikit.widget.optionsmenu.view.MenuItemImpl
import com.kelin.uikit.popup.BasicPopupWindow
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.pop_common_popup_menu.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.IllegalStateException
import java.lang.RuntimeException

/**
 * **描述:** 通用的PopupMenu。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/2/15  4:27 PM
 *
 * **版本:** v 1.0.0
 */
class OptionsMenu private constructor(
    context: Activity,
    actionMenuView: ActionMenuView?,
    anchorMenu: MenuItem?,
    anchorView: View?,
    @MenuRes menuRes: Int,
    private val onItemClickListener: ((item: MenuItem) -> Unit)?
) : BasicPopupWindow(context) {


    private val menuItemCache: SparseArray<MenuItem> = SparseArray()
    private var onMenuItemClickListener: Window.Callback? = null

    private val menuClickListener = MenuItem.OnMenuItemClickListener { menuItem ->
        dismiss()
        if (onItemClickListener != null) {
            onItemClickListener.invoke(menuItem)
        } else {
            onMenuItemClickListener?.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, menuItem)
        }
        true
    }

    init {
        onMenuItemClickListener = if (onItemClickListener == null) {
            if (actionMenuView != null && anchorMenu != null) {
                anchorMenu.setOnMenuItemClickListener {
                    showMenu()
                    true
                }
                (actionMenuView.context as Activity).window.callback
            } else if (anchorView != null) {
                anchorView.setOnClickListener {
                    showMenu()
                }
                (anchorView.context as Activity).window.callback
            } else {
                throw IllegalStateException("Null at all error")
            }
        } else {
            null
        }
        inflate(menuRes)
        llContentGroup.setOnClickListener { dismiss() }
    }

    private fun inflate(menuRes: Int) {
        var parser: XmlResourceParser? = null
        try {
            parser = context.resources.getLayout(menuRes)
            val attrs = Xml.asAttributeSet(parser)
            parseMenu(parser, attrs)
        } catch (e: XmlPullParserException) {
            throw InflateException("Error inflating menu XML", e)
        } catch (e: IOException) {
            throw InflateException("Error inflating menu XML", e)
        } finally {
            parser?.close()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseMenu(parser: XmlPullParser, attrs: AttributeSet) {

        var eventType = parser.eventType
        var tagName: String
        var lookingForEndOfUnknownTag = false
        var unknownTagName: String? = null

        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.name
                if (tagName == XML_MENU) {
                    // Go to next tag
                    eventType = parser.next()
                    break
                }

                throw RuntimeException("Expecting menu, got $tagName")
            }
            eventType = parser.next()
        } while (eventType != XmlPullParser.END_DOCUMENT)

        var reachedEndOfMenu = false
        var menuItem: MenuItem? = null
        loop@ while (!reachedEndOfMenu) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (lookingForEndOfUnknownTag) {
                        break@loop
                    }

                    tagName = parser.name
                    when (tagName) {
                        XML_GROUP -> throw IllegalTagException("the tag:$tagName not support!")
                        XML_MENU -> {
                            //暂不支持子menu。
                            throw IllegalTagException("submenu not support!")
                        }
                        XML_ITEM -> {
                            menuItem = readItem(attrs)
                        }
                        else -> {
                            lookingForEndOfUnknownTag = true
                            unknownTagName = tagName
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    tagName = parser.name
                    if (lookingForEndOfUnknownTag && tagName == unknownTagName) {
                        lookingForEndOfUnknownTag = false
                        unknownTagName = null
                    } else if (tagName == XML_GROUP) {
                        throw IllegalTagException("the tag:$tagName not support!")
                    } else if (tagName == XML_ITEM) {
                        // Add the item if it hasn't been added (if the item was
                        // a submenu, it would have been added already)
                        if (menuItem != null) {
                            menuItemCache.put(menuItem.itemId, menuItem)
                            val itemView = (menuItem as LayoutContainer).containerView!!
                            itemView.setTag(KEY_MENU_ID, menuItem.itemId)
                            llMenuRoot.addView(itemView)
                            menuItem.setOnMenuItemClickListener(menuClickListener)
                        }
                    } else if (tagName == XML_MENU) {
                        reachedEndOfMenu = true
                    }
                }

                XmlPullParser.END_DOCUMENT -> throw RuntimeException("Unexpected end of document")
            }

            eventType = parser.next()
        }
    }

    @SuppressLint("PrivateResource")
    private fun readItem(attrs: AttributeSet): MenuItem {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MenuItem)
        val item = MenuItemImpl(a.getResourceId(R.styleable.MenuItem_android_id, View.NO_ID), context)
        item.title = a.getText(R.styleable.MenuItem_android_title)
        item.setIcon(a.getResourceId(R.styleable.MenuItem_android_icon, 0))
        item.isCheckable = a.getBoolean(R.styleable.MenuItem_android_checkable, false)
        item.isChecked = a.getBoolean(R.styleable.MenuItem_android_checked, false)
        item.isVisible = a.getBoolean(R.styleable.MenuItem_android_visible, true)
        item.isEnabled = a.getBoolean(R.styleable.MenuItem_android_enabled, true)
        a.recycle()
        return item
    }

    override fun getRootLayoutResId() = R.layout.pop_common_popup_menu

    fun findItem(menuId: Int): MenuItem {
        return menuItemCache.get(menuId)
            ?: throw Resources.NotFoundException("menuItem not found,id:$menuId")
    }

    private fun showMenu() {
        val visibleItems = ArrayList<MenuItem>()
        for (i: Int in 0 until llMenuRoot.childCount) {
            val menuItem = menuItemCache[llMenuRoot.getChildAt(i).getTag(KEY_MENU_ID) as Int]
            if (menuItem.isVisible) {
                visibleItems.add(menuItem)
            }
        }
        visibleItems.forEachIndexed { i, menuItem ->
            (menuItem as LayoutContainer).containerView!!.setBackgroundResource(
                when {
                    visibleItems.size == 1 -> R.drawable.selector_white_item_bg_5r
                    i == 0 -> R.drawable.selector_white_item_bg_5r_tl_tr
                    i == visibleItems.size - 1 -> R.drawable.selector_white_item_bg_5r_bl_br
                    else -> R.drawable.selector_white_item_bg
                }
            )
        }
        showPopupWindow()
    }

    override fun getAnimViewId() = R.id.llContentGroup

    override fun getShowAnimationResId() = R.anim.anim_popup_menu_in

    override fun getExitAnimationResId() = R.anim.anim_popup_menu_out

    companion object {
        /** Menu tag name in XML.  */
        private const val XML_MENU = "menu"

        /** Group tag name in XML.  */
        private const val XML_GROUP = "group"

        /** Item tag name in XML.  */
        private const val XML_ITEM = "item"

        /**
         * 用来存储菜单ID。
         */
        private const val KEY_MENU_ID = 0x7000_00f1

        fun attachToMenu(toolbar: Toolbar, @IdRes anchorMenu: Int, @MenuRes menuRes: Int, onItemClickListener: ((item: MenuItem) -> Unit)? = null): OptionsMenu {
            val field = toolbar.javaClass.getDeclaredField("mMenuView")
            field.isAccessible = true
            val actionMenuView = field.get(toolbar) as ActionMenuView
            return OptionsMenu(toolbar.context as Activity, actionMenuView, actionMenuView.menu.findItem(anchorMenu), toolbar.getChildAt(4), menuRes, onItemClickListener)
        }

        fun attachToView(anchorView: View, @MenuRes menuRes: Int, onItemClickListener: ((item: MenuItem) -> Unit)? = null): OptionsMenu {
            return OptionsMenu(anchorView.context as Activity, null, null, anchorView, menuRes, onItemClickListener)
        }
    }
}