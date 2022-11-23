package com.kelin.uikit.widget.formview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.kelin.uikit.R
import com.kelin.uikit.dp2px
import com.kelin.uikit.getString
import com.kelin.uikit.setMarginEnd
import kotlinx.android.synthetic.main.view_form.view.*
import java.util.*
import java.util.regex.Pattern

/**
 * **描述:** 表单控件
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-22 19:11
 *
 * **版本:** v 1.0.0
 */
class FormView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        fun checkDecimal(decimal: Editable?, digits: Int, maxLength: Int = 12) {
            if (decimal != null) {
                val realDigits = digits + 1
                val index = decimal.toString().indexOf(".")
                if (index == 0) {
                    decimal.insert(0, "0")
                } else if (index > 0 && index < decimal.length - realDigits) {
                    decimal.replace(index + realDigits, decimal.length, "")
                } else if (index < 0 && decimal.length > maxLength) {
                    decimal.replace(maxLength, decimal.length, "")
                } else if (decimal.length > maxLength + realDigits) {
                    decimal.replace(decimal.length - 1, decimal.length, "")
                }
            }
        }
    }

    private val containerView by lazy { layoutInflater.inflate(R.layout.view_form, this, false) }

    private val keyListener by lazy { FormKeyListener() }

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    private var partingLineView: View? = null
    private var decimalFormat: DecimalFormat? = null

    /**
     * 是否设置过导航图标。
     */
    private var hasSet = false

    private val defGuidelinePercent: Float
        get() = if (Locale.getDefault() == Locale.CHINA) 0.31F else 0.5F

    var isEditable: Boolean = false
        set(editable) {
            if (!hasSet || editable != field) {
                field = editable
                if (editable) { //输入模式
                    val etFormViewValue = containerView.etFormViewValue
                    containerView.rtvFormViewValue.visibility = View.GONE
                    etFormViewValue.visibility = View.VISIBLE
                    etFormViewValue.setText(value)
                    etFormViewValue.hint = hint
                } else { //选择模式
                    val rtvFormViewValue = containerView.rtvFormViewValue
                    containerView.etFormViewValue.visibility = View.GONE
                    rtvFormViewValue.visibility = View.VISIBLE
                    rtvFormViewValue.text = value
                    rtvFormViewValue.hint = hint
                }
            }
        }

    var navigationIcon: Drawable? = null
        set(icon) {
            if (!hasSet || icon != field) {
                hasSet = true
                field = icon
                if (icon != null) {
                    icon.setBounds(0, 0, icon.minimumWidth, icon.minimumHeight)
                    containerView.tvFormViewUnitAndNavigation.apply {
                        setCompoundDrawables(null, null, icon, null)
                        visibility = View.VISIBLE
                    }
                    rtvFormViewValue?.setMarginEnd(0, true)
                } else {
                    containerView.tvFormViewUnitAndNavigation.visibility = View.GONE
                    rtvFormViewValue?.setMarginEnd(context.resources.getDimension(R.dimen.common_start_end_space).toInt(), true)
                }
            }
        }

    val navigationView: View
        get() = containerView.tvFormViewUnitAndNavigation

    val valueView: View
        get() = if (isEditable) {
            containerView.etFormViewValue
        } else {
            containerView.rtvFormViewValue
        }

    var value: String? = ""
        set(value) {
            field = value
            if (isEditable) {
                containerView.etFormViewValue.apply {
                    setText(value)
                    if (!value.isNullOrEmpty()) {
                        setSelection(text?.length ?: 0)
                    }
                }
            } else {
                containerView.rtvFormViewValue.text = value
            }
        }
        get() = if (isEditable) {
            containerView.etFormViewValue.text?.toString()
        } else {
            field
        }

    var hint: String = ""
        set(value) {
            if (field != value) {
                field = value
                if (isEditable) {
                    containerView.etFormViewValue.hint = value
                } else {
                    containerView.rtvFormViewValue.hint = value
                }
            }
        }

    var name: String = ""
        set(value) {
            if (field != value) {
                field = value
                containerView.rtvFormViewName.text = if (isRequiredField) {
                    "<rich color=#FF0000>*</rich>${value}"
                } else {
                    value
                }
            }
        }

    var isRequiredField: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                containerView.rtvFormViewName.text = if (value) {
                    "<rich color=#FF0000>*</rich>${name}"
                } else {
                    " $name"
                }
            }
        }

    var unit: String = ""
        set(value) {
            if (field != value) {
                field = value
                if (value.isEmpty()) {
                    if (navigationIcon == null) {
                        containerView.tvFormViewUnitAndNavigation.visibility = View.GONE
                    } else {
                        containerView.tvFormViewUnitAndNavigation.visibility = View.VISIBLE
                    }
                } else {
                    containerView.tvFormViewUnitAndNavigation.visibility = View.VISIBLE
                    containerView.tvFormViewUnitAndNavigation.text = unit
                }
            }
        }

    var maxLength: Int = 0
        @SuppressLint("SetTextI18n")
        set(value) {
            if (field != value) {
                field = value
                if (value > 0 && isEditable) {
                    val filters = containerView.etFormViewValue.filters.toMutableList()
                    filters.add(InputFilter.LengthFilter(maxLength))
                    filters.add(emojiFilter)
                    containerView.etFormViewValue.filters = filters.toTypedArray()
                    containerView.tvTextCounter.text = "${containerView.etFormViewValue.text?.length ?: 0}/${value}"
                }
            }
        }


    @ColorInt
    var valueColor: Int = Color.BLACK
        set(value) {
            field = value
            containerView.etFormViewValue.setTextColor(value)
            containerView.rtvFormViewValue.setTextColor(value)
        }

    var valueTextSizeSp: Float = 14F
        set(value) {
            field = value
            containerView.etFormViewValue.textSize = value
            containerView.rtvFormViewValue.textSize = value
        }

    var valueTextSizePx: Float = 14F
        set(value) {
            field = value
            containerView.etFormViewValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            containerView.rtvFormViewValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    var fieldNameSizeSp: Float = 14F
        set(value) {
            field = value
            containerView.rtvFormViewName.textSize = value
        }

    var fieldNameSizePx: Float = 14F
        set(value) {
            field = value
            containerView.rtvFormViewName.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    var inputType: Int = EditorInfo.TYPE_NULL
        set(value) {
            if (field != value) {
                field = value
                containerView.etFormViewValue.inputType = value
            }
        }

    var noPartingLine: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    if (partingLineView != null) {
                        partingLineView!!.visibility = View.GONE
                    }
                } else {
                    if (partingLineView == null) {
                        addPartingLine(layoutInflater)
                    } else {
                        partingLineView!!.visibility = View.VISIBLE
                    }
                }
            }
        }

    var valueTypeface: Typeface?
        set(value) {
            containerView.rtvFormViewValue.typeface = value
        }
        get() = containerView.rtvFormViewValue.typeface


    private val emojiFilter = object : InputFilter {

        private var emoji = Pattern.compile("[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE)

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            return if (emoji.matcher(source).find()) "" else null
        }
    }

    private val textChangeListener by lazy { TextWatcherImpl() }

    private val guidelinePercent: Float

    init {
        addView(containerView)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FormView)

        if (ta.getBoolean(R.styleable.FormView_centerVertical, true)) {
            (containerView.rtvFormViewName.layoutParams as ConstraintLayout.LayoutParams).verticalBias = 0.5F
        }

        //值的gravity属性。
        ta.getInt(R.styleable.FormView_valueGravity, 0x03).also {
            val gravity = when (it) {
                0x01 -> Gravity.CENTER_VERTICAL or Gravity.START
                0x02 -> Gravity.CENTER
                else -> Gravity.CENTER_VERTICAL or Gravity.END
            }
            containerView.etFormViewValue.gravity = gravity
            containerView.rtvFormViewValue.gravity = gravity
        }

        ta.getDrawable(R.styleable.FormView_android_drawableStart)?.also {
            it.setBounds(0, 0, it.minimumWidth, it.minimumHeight)
            containerView.rtvFormViewName.apply {
                setCompoundDrawables(it, null, null, null)
                compoundDrawablePadding =
                    ta.getDimensionPixelOffset(R.styleable.FormView_android_drawablePadding, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10F, context.resources.displayMetrics).toInt())
            }
        }

        guidelinePercent = ta.getFloat(R.styleable.FormView_guidelinePercent, -0.1f)

        val ni = ta.getDrawable(R.styleable.FormView_navigationIcon)
        isEditable = ta.getBoolean(R.styleable.FormView_editable, ni == null)
        if (ta.getBoolean(R.styleable.FormView_smartBackgroundEnable, true)) {
            setBackgroundResource(if (isEditable) R.color.color_white else R.drawable.selector_recycler_item_bg)
        }
        navigationIcon = ni
        //处理编辑模式下的特殊输入类型以及默认键盘。
        ta.getInt(R.styleable.FormView_keyType, 0).apply {
            if (this != 0) {
                keyListener.currentInputType = and(0x0F)
                keyListener.setAcceptedCharsType(and(0xF0))
                containerView.etFormViewValue.post { containerView.etFormViewValue.keyListener = keyListener }
            }
        }
        isRequiredField = ta.getBoolean(R.styleable.FormView_requiredField, false)
        name = ta.getString(R.styleable.FormView_fieldName) ?: ""
        hint = ta.getBoolean(R.styleable.FormView_smartHintEnable, false).let { enable ->
            if (enable) {
                "${if (isEditable) getString(R.string.please_input) else getString(R.string.please_select)}${name}"
            } else {
                ta.getString(R.styleable.FormView_android_hint) ?: ""
            }
        }
        value = ta.getString(R.styleable.FormView_fieldValue) ?: ""
        unit = ta.getString(R.styleable.FormView_fieldUnit) ?: ""
        ta.getColor(R.styleable.FormView_fieldUnitColor, -1).apply {
            if (this != -1) {
                containerView.tvFormViewUnitAndNavigation.setTextColor(this)
            }
        }
        ta.getColor(R.styleable.FormView_fieldValueColor, -1).apply {
            if (this != -1) {
                containerView.etFormViewValue.setTextColor(this)
                containerView.rtvFormViewValue.setTextColor(this)
            }
        }

        ta.getDimension(R.styleable.FormView_fieldValueSize, -1F).apply {
            if (this != -1F) {
                valueTextSizePx = this
            }
        }

        ta.getDimension(R.styleable.FormView_fieldNameSize, -1F).apply {
            if (this != -1F) {
                fieldNameSizePx = this
            }
        }

        maxLength = ta.getInt(R.styleable.FormView_android_maxLength, Int.MAX_VALUE)
        inputType = ta.getInt(R.styleable.FormView_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
        noPartingLine = ta.getBoolean(R.styleable.FormView_noPartingLine, false)
        val split = ta.getString(R.styleable.FormView_decimalFormat)?.trim()?.split(",")?.mapNotNull {
            try {
                it.toInt()
            } catch (ignore: Exception) {
                null
            }
        }
        if (split != null && split.size >= 2) {
            decimalFormat = DecimalFormat(split[0], split[1])
        }
        containerView.tvTextCounter.visibility = if (ta.getBoolean(R.styleable.FormView_counterEnable, false)) {
            containerView.etFormViewValue.layoutParams.height = 100.dp2px
            View.VISIBLE
        } else {
            View.GONE
        }
        ta.recycle()
        containerView.etFormViewValue.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as EditText).addTextChangedListener(textChangeListener)
            } else {
                (v as EditText).removeTextChangedListener(textChangeListener)
            }
        }
    }

    fun setNavigationClickListener(l: () -> Unit) {
        containerView.tvFormViewUnitAndNavigation.setOnClickListener { l() }
    }

    override fun onAttachedToWindow() {
        val p = parent as? FormLayout
        fvGuideline.setGuidelinePercent(if (guidelinePercent == -0.1f && p != null) p.guidelinePercent else if (guidelinePercent < 0) defGuidelinePercent else guidelinePercent)
        super.onAttachedToWindow()
    }

    override fun onFinishInflate() {
        if (childCount > 2) {
            for (i: Int in 2 until childCount) {
                val child = getChildAt(i)
                removeView(child)
                clContainer.addView(child, child.layoutParams)
            }
            clContainer.visibility = View.VISIBLE
            containerView.etFormViewValue.visibility = View.GONE
            containerView.rtvFormViewValue.visibility = View.GONE
        }
        super.onFinishInflate()
    }

    private fun addPartingLine(inflater: LayoutInflater) {
        partingLineView = inflater.inflate(R.layout.common_horizontal_parting_line, this, false)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, 1)
        //设置margin。
//        val margin = Units.dp2px(context, 20F)
//        params.setMargins(margin, 0, margin, 0)
        params.addRule(ALIGN_PARENT_BOTTOM)
        addView(partingLineView, params)
    }

    fun setAfterTextChangedListener(onTextChangedListener: (formView: FormView, text: Editable) -> Unit) {
        textChangeListener.onTextChangedListener = onTextChangedListener
    }

    private inner class DecimalFormat(val integerLength: Int, val floatLength: Int)

    private inner class TextWatcherImpl : TextWatcher {

        var onTextChangedListener: ((formView: FormView, text: Editable) -> Unit)? = null

        @SuppressLint("SetTextI18n")
        override fun afterTextChanged(s: Editable?) {
            if (containerView.etFormViewValue.hasFocus() && s != null) {
                if (decimalFormat != null) {
                    checkDecimal(s, decimalFormat!!.floatLength, decimalFormat!!.integerLength)
                }
                onTextChangedListener?.invoke(this@FormView, s)
                if (containerView.tvTextCounter.visibility == View.VISIBLE) {
                    containerView.tvTextCounter.text = "${s.length}/${maxLength}"
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private inner class FormKeyListener : DigitsKeyListener() {

        private val smallLetter by lazy { "abcdefghijklmnopqrstuvwxyz".toCharArray() }
        private val capitalLetter by lazy { "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray() }
        private val letterNumber by lazy { "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".toCharArray() }
        private val smallLetterNumber by lazy { "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray() }
        private val capitalLetterNumber by lazy { "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray() }

        private var currentCharArray: CharArray = letterNumber
        var currentInputType: Int = InputType.TYPE_CLASS_PHONE

        fun setAcceptedCharsType(type: Int) {
            currentCharArray = when (type) {
                0x10 -> smallLetter
                0x20 -> capitalLetter
                0x30 -> letterNumber
                0x40 -> smallLetterNumber
                0x50 -> capitalLetterNumber
                else -> letterNumber
            }
        }

        override fun getInputType(): Int = currentInputType
        override fun getAcceptedChars(): CharArray = currentCharArray
    }
}