package com.kelin.uikit.widget.richtextview

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.getSpans
import com.kelin.uikit.R
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * **描述:** 富文本TextView.
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-11-25  13:44
 *
 * **版本:** v 1.0.0
 */
class RichTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var richSeeMoreText: CharSequence? = null
    private var seeMoreTextColor: String? = null
    private var seeMoreTextStyle: String = ""
    private var seeMoreTextSize: Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        14F,
        context.resources.displayMetrics
    ).toInt()
    private var seeMoreTextPadding: Int = 0
    private var fullText: CharSequence? = null
    private val realWidth: Int
        get() = measuredWidth - paddingLeft - paddingRight

    private var hasClickListener = false
    private var hasLongClickListener = false

    private val lastLineNumber: Int
        get() = maxLines - 1

    private var linkClickListener: ((flag: String) -> Unit)? = null


    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.RichTextView)
            val sourceId = ta.getResourceId(R.styleable.RichTextView_textRawResId, View.NO_ID)
            if (sourceId != View.NO_ID) {
                text = getStringFromStream(context.resources.openRawResource(sourceId))
            }
            val st = ta.getString(R.styleable.RichTextView_seeMoreText)
            seeMoreTextColor = ta.getString(R.styleable.RichTextView_seeMoreTextColor)
            seeMoreTextSize =
                ta.getDimensionPixelSize(R.styleable.RichTextView_seeMoreTextSize, seeMoreTextSize)
            seeMoreTextStyle =
                mapperStyleByFlag(ta.getInt(R.styleable.RichTextView_seeMoreTextStyle, 0))
            seeMoreTextPadding =
                ta.getDimensionPixelSize(R.styleable.RichTextView_seeMoreTextPadding, 0)
            if (!st.isNullOrEmpty()) {
                val clickViewSeeMore =
                    ta.getBoolean(R.styleable.RichTextView_clickViewSeeMore, false)
                val seeMoreText = fixedSeeMoreText(
                    st,
                    seeMoreTextColor,
                    seeMoreTextSize,
                    seeMoreTextStyle,
                    !clickViewSeeMore
                )
                if (clickViewSeeMore) {
                    setOnClickListener { onTextClick(RICH_TAG_SEE_MORE) }
                } else {
                    movementMethod = LinkMovementMethod()
                    highlightColor = Color.TRANSPARENT
                }
                richSeeMoreText = RichTextHelper(paint).fromText(seeMoreText)
            }
            ta.recycle()
        }
    }

    fun setOnLinkClickListener(l: (flag: String) -> Unit) {
        linkClickListener = l
    }

    private val hasClickableSpan: Boolean
        get() = (text as? Spannable)?.getSpans<ClickableSpan>(0, text.length)?.isNotEmpty() ?: false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (richSeeMoreText?.isNotEmpty() == true && maxLines < lineCount && !fullText.isNullOrEmpty()) {
            handTextMeasure()
        }
    }

    private fun getStringFromStream(inputStream: InputStream): String {
        val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
        val reader = BufferedReader(inputStreamReader)
        val sb = StringBuffer("")
        var line = reader.readLine()
        while (line != null) {
            sb.append(line)
            sb.append("<br/>")
            line = reader.readLine()
        }
        return sb.toString()
    }

    fun setTextResource(@RawRes resourceId: Int) {
        text = getStringFromStream(context.resources.openRawResource(resourceId))
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        hasClickListener = l != null
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        super.setOnLongClickListener(l)
        hasLongClickListener = l != null
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (text != null && text is String && text.contains("<rich")) {
            val textHelper = RichTextHelper(paint)
            fullText = textHelper.fromText(text)
            if (text.contains("clickable=")) {
                movementMethod = FixedLinkMovementMethod()
                highlightColor = Color.TRANSPARENT
                textHelper.setOnLinkClickListener { onTextClick(it) }
                isClickable = false
                isLongClickable = false
                isFocusable = false
            } else {
                isClickable = hasClickListener
                isLongClickable = hasLongClickListener
                isFocusable = hasClickListener or hasLongClickListener
            }
            super.setText(fullText, type)
        } else {
            isClickable = hasClickListener
            isLongClickable = hasLongClickListener
            isFocusable = hasClickListener or hasLongClickListener
            super.setText(text, type)
        }
    }

    private fun createLayout(source: CharSequence): Layout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(source, 0, source.length, paint, realWidth)
                .build()
        } else {
            StaticLayout(source, paint, realWidth, Layout.Alignment.ALIGN_NORMAL, 1F, 0F, false)
        }
    }


    private fun fixedSeeMoreText(
        st: String,
        stColor: String?,
        stSize: Int,
        stStyle: String,
        needClick: Boolean
    ): String {
        return "${RichTextHelper.TAG_RICH_PREFIX} ${if (stColor.isNullOrEmpty()) "" else "color=$stColor"} size=${stSize}px ${if (stStyle.isEmpty()) "" else stStyle} ${if (needClick) "clickable=$RICH_TAG_SEE_MORE" else ""}>$st${RichTextHelper.TAG_RICH_END}"
    }

    private fun mapperStyleByFlag(styleFlag: Int): String {
        return when (styleFlag) {
            STYLE_BOLD -> "style=b"
            STYLE_ITALIC -> "style=i"
            STYLE_BOLD_ITALIC -> "style=b_i"
            STYLE_BOLD_UNDERLINE -> "style=b_u"
            STYLE_ITALIC_UNDERLINE -> "style=i_u"
            STYLE_ITALIC_UNDERLINE_BOLD -> "style=b_i_u"
            else -> ""
        }
    }

    private fun onTextClick(flag: String) {
        if (flag == RICH_TAG_SEE_MORE) {
            maxLines = Int.MAX_VALUE
            text = fullText
        } else {
            linkClickListener?.invoke(flag)
        }
    }

    private fun handTextMeasure() {
        val st = richSeeMoreText
        richSeeMoreText = null
        if (!st.isNullOrEmpty()) {
            val sw =
                ((createLayout(st).getLineWidth(0) + seeMoreTextPadding + paint.measureText(ELLIPSIS)) * 1.1).toInt()
            if (realWidth >= sw) {
                val lastLine = getLastLineText(lastLineNumber)
                if (lastLine != null) {
                    text = when {
                        lastLine.lastLineText.isEmpty() -> SpannableStringBuilder(
                            text.subSequence(
                                layout.getLineStart(0),
                                layout.getLineEnd(lastLineNumber - 1)
                            )
                        ).append(st)
                        lastLine.lastLineNumber == lastLineNumber -> when {
                            layout.getLineWidth(lastLineNumber) + sw < realWidth -> SpannableStringBuilder(
                                text.subSequence(
                                    layout.getLineStart(0),
                                    layout.getLineEnd(lastLineNumber - 1)
                                )
                            ).append(lastLine.lastLineText)
                                .append(ELLIPSIS)
                                .append(st)
                            lastLine.lastLineText.length > 3 -> SpannableStringBuilder(
                                text.subSequence(
                                    layout.getLineStart(0),
                                    layout.getLineEnd(lastLineNumber - 1)
                                )
                            )
                                .append(
                                    getShowingText(
                                        lastLine.lastLineText.subSequence(
                                            0,
                                            lastLine.lastLineText.length - 3
                                        ), sw
                                    )
                                )
                                .append(ELLIPSIS)
                                .append(st)
                            else -> SpannableStringBuilder(
                                text.subSequence(
                                    layout.getLineStart(0),
                                    layout.getLineEnd(lastLineNumber - 1)
                                )
                            )
                                .append(st)
                        }
                        else -> SpannableStringBuilder(
                            text.subSequence(
                                layout.getLineStart(0),
                                layout.getLineEnd(lastLine.lastLineNumber - 1)
                            )
                        )
                            .append(
                                getShowingText(
                                    lastLine.lastLineText.subSequence(
                                        0,
                                        lastLine.lastLineText.length - 3
                                    ), sw
                                )
                            )
                            .append(ELLIPSIS)
                            .append(st)
                    }
                }
            } else {
                throw RuntimeException("The view must be wider than showMoreText.")
            }
        }
    }

    private fun getLastLineText(lineNumber: Int): LastLineInfo? {
        val t = text.subSequence(layout.getLineStart(lineNumber), layout.getLineEnd(lineNumber))
        return if (t.length == 1 && t.contains("\n")) {
            if (lineNumber > 0) {
                getLastLineText(lineNumber - 1)
            } else {
                null
            }
        } else {
            LastLineInfo(
                if (t.endsWith("\n")) {
                    t.subSequence(0, t.length - 1)
                } else {
                    t
                }, lineNumber
            )
        }
    }

    private fun getShowingText(text: CharSequence, showMoreTextWidth: Int): CharSequence {
        return if (paint.measureText(text, 0, text.length) + showMoreTextWidth <= realWidth) {
            text
        } else {
            getShowingText(text.subSequence(0, text.length - 1), showMoreTextWidth)
        }
    }

    companion object {
        private const val RICH_TAG_SEE_MORE = "rich_tag_see_more"
        private const val ELLIPSIS = "... "
        private const val STYLE_BOLD = 0x10
        private const val STYLE_ITALIC = 0x11
        private const val STYLE_BOLD_ITALIC = 0x12
        private const val STYLE_BOLD_UNDERLINE = 0x13
        private const val STYLE_ITALIC_UNDERLINE = 0x14
        private const val STYLE_ITALIC_UNDERLINE_BOLD = 0x15
    }

    private inner class LastLineInfo(val lastLineText: CharSequence, val lastLineNumber: Int)

    private inner class FixedLinkMovementMethod : LinkMovementMethod() {
        override fun onTouchEvent(widget: TextView, buffer: Spannable?, event: MotionEvent): Boolean {
            val action = event.action

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                var x = event.x.toInt()
                var y = event.y.toInt()
                x -= widget.totalPaddingLeft
                y -= widget.totalPaddingTop
                x += widget.scrollX
                y += widget.scrollY
                val layout = widget.layout
                val line = layout.getLineForVertical(y)
                val off = layout.getOffsetForHorizontal(line, x.toFloat())
                val links = buffer!!.getSpans(off, off, ClickableSpan::class.java)
                if (links.isNotEmpty()) {
                    val link = links[0]
                    if (action == MotionEvent.ACTION_UP) {
                        link.onClick(widget)
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(
                            buffer,
                            buffer.getSpanStart(link),
                            buffer.getSpanEnd(link)
                        )
                    }
                    return true
                } else {
                    Selection.removeSelection(buffer)
                    if (action == MotionEvent.ACTION_UP && !widget.isClickable) {
                        widget.performClick()
                    }
                }
            }

            return super.onTouchEvent(widget, buffer, event)
        }
    }
}