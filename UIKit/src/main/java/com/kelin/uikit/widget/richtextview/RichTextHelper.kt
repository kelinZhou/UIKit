package com.kelin.uikit.widget.richtextview

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.style.*
import android.view.View
import org.xml.sax.XMLReader

/**
 * **描述: ** Html标签处理
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2018/7/11  下午3:40
 *
 * **版本: ** v 1.0.0
 */
internal class RichTextHelper(val paint: TextPaint) : Html.TagHandler {

    companion object {
        internal const val TAG_RICH = "rich"
        internal const val TAG_RICH_PREFIX = "<rich"
        internal const val TAG_RICH_END = "</rich>"
    }

    private var currentTagInfo: TagInfo? = null

    private var onTextClickListener: ((flag: String) -> Unit)? = null

    fun setOnLinkClickListener(l: (flag: String) -> Unit) {
        onTextClickListener = l
    }

    internal fun fromText(source: String): CharSequence {
        val ns = source.replace("\n", "<br/>", true)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(
                if (ns.startsWith("<html>")) ns else "<html>$ns</html>",
                Html.FROM_HTML_MODE_LEGACY,
                null,
                this
            )
        } else {
            Html.fromHtml(
                if (ns.startsWith("<html>")) ns else "<html>$ns</html>",
                null,
                this
            )
        }
    }

    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        if (opening) {
            handlerStartTAG(tag, output, xmlReader)
        } else {
            handlerEndTAG(tag, output)
        }
    }

    private fun handlerStartTAG(tag: String, output: Editable, xmlReader: XMLReader) {
        if (tag.equals(TAG_RICH, ignoreCase = true)) {
            handlerRichStart(output, xmlReader)
        }
    }

    private fun handlerEndTAG(tag: String, output: Editable) {
        if (tag.equals(TAG_RICH, ignoreCase = true)) {
            handlerRichEnd(output)
        }
    }

    private fun handlerRichStart(output: Editable, xmlReader: XMLReader) {
        val index = output.length
        val tagInfo = TagInfo(index)

        val style = getProperty(xmlReader, "style")
        if (!style.isNullOrEmpty()) {
            tagInfo.style = when (style) {
                "b", "bold" -> Typeface.BOLD
                "i", "italic" -> Typeface.ITALIC
                "b_i", "i_b", "bold_italic", "italic_bold" -> Typeface.BOLD_ITALIC
                "u", "underline" -> {
                    tagInfo.hasUnderline = true
                    Typeface.NORMAL
                }
                "i_u", "u_i", "italic_underline", "underline_italic" -> {
                    tagInfo.hasUnderline = true
                    Typeface.ITALIC
                }
                "b_u", "u_b", "bold_underline", "underline_bold" -> {
                    tagInfo.hasUnderline = true
                    Typeface.BOLD
                }
                "b_u_i",
                "b_i_u",
                "u_b_i",
                "u_i_b",
                "i_u_b",
                "i_b_u",
                "italic_bold_underline",
                "italic_underline_bold",
                "underline_italic_bold",
                "underline_bold_italic",
                "bold_underline_italic",
                "bold_italic_underline" -> {
                    tagInfo.hasUnderline = true
                    Typeface.BOLD_ITALIC
                }
                else -> Typeface.NORMAL
            }
        }
        val clickable = getProperty(xmlReader, "clickable")
        if (!clickable.isNullOrEmpty()) {
            tagInfo.clickable = clickable
        }
        val size = getProperty(xmlReader, "size")
        if (!size.isNullOrEmpty()) {
            tagInfo.size = when {
                size.endsWith("sp", true) -> Integer.parseInt(size.replace("sp", "", true))
                size.endsWith("px", true) -> {
                    tagInfo.sizeDip = false
                    Integer.parseInt(size.replace("px", "", true))
                }
                else -> try {
                    Integer.parseInt(size)
                } catch (e: Exception) {
                    20
                }
            }
        }
        val color = getProperty(xmlReader, "color")
        if (!color.isNullOrEmpty()) {
            tagInfo.color = color
        }
        val bg = getProperty(xmlReader, "bg")
        if (!bg.isNullOrEmpty()) {
            tagInfo.bgColor = bg
        }
        val marginStart = getProperty(xmlReader, "retract")?.toIntOrNull()
        if (marginStart != null) {
            tagInfo.marginStart = marginStart
        }
        currentTagInfo = tagInfo
    }

    private fun handlerRichEnd(output: Editable) {
        val tagInfo = currentTagInfo
        if (tagInfo != null) {
            val color = tagInfo.color
            val bg = tagInfo.bgColor
            val size = tagInfo.size
            val style = tagInfo.style
            val clickable = tagInfo.clickable
            val end = output.length
            val margin = tagInfo.marginStart
            if (margin > 0) {
                output.setSpan(LeadingMarginSpan.Standard(paint.measureText("a").toInt() * margin, 0), tagInfo.startIndex, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            }
            if (!clickable.isNullOrEmpty()) {
                output.setSpan(RichClickableSpan(clickable), tagInfo.startIndex, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (!bg.isNullOrEmpty()) {
                output.setSpan(
                    BackgroundColorSpan(Color.parseColor(bg)),
                    tagInfo.startIndex,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (!color.isNullOrEmpty()) {
                output.setSpan(
                    ForegroundColorSpan(Color.parseColor(color)),
                    tagInfo.startIndex,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (size > 0) {
                output.setSpan(
                    AbsoluteSizeSpan(size, tagInfo.sizeDip),
                    tagInfo.startIndex,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (style != Typeface.NORMAL) {
                output.setSpan(StyleSpan(style), tagInfo.startIndex, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (tagInfo.hasUnderline) {
                output.setSpan(UnderlineSpan(), tagInfo.startIndex, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    /**
     * 利用反射获取html标签的属性值
     */
    @Suppress("UNCHECKED_CAST")
    private fun getProperty(xmlReader: XMLReader, property: String): String? {
        try {
            val elementField = xmlReader.javaClass.getDeclaredField("theNewElement")
            elementField.isAccessible = true
            val element: Any = elementField.get(xmlReader)
            val attsField = element.javaClass.getDeclaredField("theAtts")
            attsField.isAccessible = true
            val atts: Any = attsField.get(element)
            val dataField = atts.javaClass.getDeclaredField("data")
            dataField.isAccessible = true
            val data = dataField.get(atts) as Array<String>
            val lengthField = atts.javaClass.getDeclaredField("length")
            lengthField.isAccessible = true
            val len = lengthField.getInt(atts)
            for (i in 0 until len) {
                // 判断属性名
                if (property == data[i * 5 + 1]) {
                    return data[i * 5 + 4]
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private inner class TagInfo(val startIndex: Int) {
        var style: Int = Typeface.NORMAL
        var hasUnderline: Boolean = false
        var clickable: String? = null
        var color: String? = null
        var size: Int = 0
            set(value) {
                if (value > 0) {
                    field = value
                }
            }
        var sizeDip: Boolean = true
        var marginStart: Int = 0
        var bgColor: String? = null
    }

    private inner class RichClickableSpan(private val flag: String) : ClickableSpan() {
        override fun onClick(widget: View) {
            onTextClickListener?.invoke(flag)
        }

        override fun updateDrawState(ds: TextPaint) {
        }
    }
}
