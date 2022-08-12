package com.kelin.uikit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.forEach
import androidx.core.widget.doAfterTextChanged
import com.kelin.uikit.tools.DateHelper
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * **描述:** 与UI相关的扩展函数。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2022/8/11 4:56 PM
 *
 * **版本:** v 1.0.0
 */

fun Any?.getString(@StringRes stringResId: Int, vararg formatArgs: Any): String {
    return ((this as? Activity)?: UIKit.getContext()).resources.let {
        if (formatArgs.isEmpty()) {
            it.getString(stringResId)
        } else {
            it.getString(stringResId, *formatArgs)
        }
    }
}

var View.visibleOrGone: Boolean
    set(value) {
        visibility = value.toVisibleOrGone()
    }
    get() = visibility == View.VISIBLE

var View.visibleOrInvisible: Boolean
    set(value) {
        visibility = value.toVisibleOrInvisible()
    }
    get() = visibility == View.VISIBLE

//View相关的
fun Boolean.toVisibleOrGone(): Int {
    return if (this) View.VISIBLE else View.GONE
}

fun Boolean.toVisibleOrInvisible(): Int {
    return if (this) View.VISIBLE else View.INVISIBLE
}

fun View?.setTransitionName(name: Any): View? {
    if (this != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        transitionName = name.toString()
    }
    return this
}

//Boolean相关的
val Boolean.intValue: Int
    get() = if (this) 1 else 0

val Int.booleanValue: Boolean
    get() = this == 1

//Activity相关的
fun Intent.start(context: Context, vararg sharedViews: View?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val views = sharedViews.filter { !it?.transitionName.isNullOrEmpty() }
        if (context is Activity && !views.isNullOrEmpty()) {
            ActivityOptionsCompat.makeSceneTransitionAnimation(context, *views.map { Pair(it, it?.transitionName ?: "") }.toTypedArray()).also {
                context.startActivity(this, it.toBundle())
            }
        } else {
            context.startActivity(this)
        }
    } else {
        context.startActivity(this)
    }

}

fun View.switchPswVisibility(pswView: EditText) {
    isSelected.negation().run {
        isSelected = this
        val start = pswView.selectionStart
        val stop = pswView.selectionEnd
        pswView.transformationMethod = if (this) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        pswView.setSelection(start, stop)
    }
}

fun View.setHeight(height: Int, refresh: Boolean = false) {
    layoutParams.height = height
    if (refresh) {
        layoutParams = layoutParams
    }
}

fun View.setMarginStart(start: Int, refresh: Boolean = false) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams
    lp?.leftMargin = start
    if (refresh && lp != null) {
        layoutParams = lp
    }
}

fun View.setMarginEnd(end: Int, refresh: Boolean = false) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams
    lp?.rightMargin = end
    if (refresh && lp != null) {
        layoutParams = lp
    }
}

fun View.setMarginTop(top: Int, refresh: Boolean = false) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams
    lp?.topMargin = top
    if (refresh && lp != null) {
        layoutParams = lp
    }
}

fun View.setMarginBottom(bottom: Int, refresh: Boolean = false) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams
    lp?.bottomMargin = bottom
    if (refresh && lp != null) {
        layoutParams = lp
    }
}

fun Bitmap.writeToAlbum(targetPath: String, context: Context, finished: (path: String?, uri: Uri?) -> Unit) {
    if (writeToFile(targetPath)) {
        MediaScannerConnection.scanFile(
            context, arrayOf(targetPath), arrayOf("image/jpeg")
        ) { path, uri ->
            finished(path, uri)
        }
    } else {
        finished(null, null)
    }
}

//Bitmap相关
fun Bitmap.writeToFile(targetPath: String): Boolean {
    return try {
        val targetFile = File(targetPath)
        if (!targetFile.exists()) {
            targetFile.parentFile?.apply {
                if (!exists()) {
                    mkdirs()
                }
            }
            if (!targetFile.exists()) {
                targetFile.createNewFile()
            }
        }
        val fos = FileOutputStream(targetPath)
        //通过io流的方式来压缩保存图片
        if (hasAlpha()) {
            compress(Bitmap.CompressFormat.PNG, 100, fos)
        } else {
            compress(Bitmap.CompressFormat.JPEG, 70, fos)
        }
        fos.flush()
        fos.close()
        targetFile.exists()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun TextView.setDrawableTop(@DrawableRes drawableRes: Int) {
    val drawable = try {
        ContextCompat.getDrawable(context, drawableRes)
    } catch (e: Exception) {
        null
    }
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(null, drawable, null, null)
}

fun TextView.setDrawableStart(@DrawableRes drawableRes: Int) {
    setDrawableStart(
        try {
            ContextCompat.getDrawable(context, drawableRes)
        } catch (e: Exception) {
            null
        }
    )
}

fun TextView.setDrawableStart(drawable: Drawable?) {
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(drawable, null, null, null)
}

fun TextView.setDrawableEnd(@DrawableRes drawableRes: Int) {
    val drawable = try {
        ContextCompat.getDrawable(context, drawableRes)
    } catch (e: Exception) {
        null
    }
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(null, null, drawable, null)
}

fun TextView.setDrawableStartAndEnd(@DrawableRes drawableStart: Int, @DrawableRes drawableEnd: Int) {
    val start = try {
        ContextCompat.getDrawable(context, drawableStart)
    } catch (e: Exception) {
        null
    }
    start?.setBounds(0, 0, start.minimumWidth, start.minimumHeight)
    val end = try {
        ContextCompat.getDrawable(context, drawableEnd)
    } catch (e: Exception) {
        null
    }
    end?.setBounds(0, 0, end.minimumWidth, end.minimumHeight)
    setCompoundDrawables(start, null, end, null)
}

inline fun <T> ViewGroup.findTarget(finder: (v: View) -> T?): T? {
    this.forEach {
        val r = finder(it)
        if (r != null) {
            return r
        }
    }
    return null
}

fun TextView.checkDecimal(digits: Int, maxLength: Int = 12) {
    doAfterTextChanged { decimal ->
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

fun Editable?.removeLast() {
    if (!isNullOrBlank()) {
        delete(length - 1, length)
    }
}

val Int.dp2px: Int
    get() = this.toFloat().dp2px

val Int.dp2pxF: Float
    get() = this.toFloat().dp2pxF

val Float.dp2px: Int
    get() = dp2pxF.toInt()

val Float.dp2pxF: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, UIKit.getContext().resources.displayMetrics)

val Int.px2dp: Int
    get() = this.toFloat().px2dp

val Float.px2dp: Int
    get() = (this * 160 / UIKit.getContext().resources.displayMetrics.densityDpi + 0.5f).toInt()

val Int.sp2px: Int
    get() = this.toFloat().sp2px

val Int.sp2pxF: Float
    get() = this.toFloat().sp2pxF

val Float.sp2px: Int
    get() = sp2pxF.toInt()

val Float.sp2pxF: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, UIKit.getContext().resources.displayMetrics)

val Int.px2sp: Int
    get() = this.toFloat().px2sp

val Float.px2sp: Int
    get() = (this / UIKit.getContext().resources.displayMetrics.scaledDensity + 0.5f).toInt()

//Boolean相关的
fun Boolean.negation(): Boolean {
    return !this
}


fun Int.isIn(vararg numbers: Int): Boolean {
    return numbers.contains(this)
}

fun Long.isIn(vararg numbers: Long): Boolean {
    return numbers.contains(this)
}

fun Long.isNotIn(vararg numbers: Long): Boolean {
    return !numbers.contains(this)
}

fun Int.isNotIn(vararg numbers: Int): Boolean {
    return !numbers.contains(this)
}

fun Any.toRichText(color: String? = "#F86666", size: Int = 0, bold: Boolean = false): String {
    return if (this is String && isEmpty()) {
        ""
    } else {
        val c = if (!color.isNullOrEmpty()) "color=${color}" else ""
        val s = if (size > 0) "size=${size}sp" else ""
        val b = if (bold) "bold=true" else ""
        "<rich $c $s $b>${this}</rich>"
    }
}

//格式化相关

/**
 * 格式化小数。
 * @param decimalDigits 要保留的小数的位数，可以不传，默认保留真实小数最大3位，例如：52 -> "52"; 53.2 -> "53.2"; 53.2421 -> "53.242"。
 * @param dollarFormat 是否要格式为美元样式(千位分隔样式)，例如：135,435,231.09；可以不传，默认为true。
 * @return 返回格式化后数字字符串。
 */
fun Double.formatToString(decimalDigits: Int = 2, dollarFormat: Boolean = true, unit: String = ""): String {
    return numberFormat(decimalDigits, dollarFormat, this) + unit
}

/**
 * 格式化小数。
 * @param unit 单位。
 * @param decimalDigits 要保留的小数的位数，可以不传，默认保留真实小数最大2位，例如：52 -> "52"; 53.2 -> "53.2"; 53.2421 -> "53.242"。
 * @param dollarFormat 是否要格式为美元样式(千位分隔样式)，例如：135,435,231.09；可以不传，默认为true。
 * @param ignoreZero 是否将0看成是没有值，如果为true当数据为0时则返回空串。
 * @return 返回格式化后数字字符串。
 */
fun Double.formatToDollar(unit: String = "", decimalDigits: Int = 2, dollarFormat: Boolean = true, ignoreZero: Boolean = false): String {
    return if (ignoreZero && this == 0.0) "" else numberFormat(decimalDigits, dollarFormat, this) + unit
}

/**
 * 格式化小数。
 * @param ignoreZero 是否忽略0，如果忽略当数值为0是返回空的字符串。
 * @param unit 单位。
 * @return 返回格式化后数字字符串。
 */
fun Float.formatToReal(ignoreZero: Boolean = false, decimalDigits: Int = -1, unit: String = ""): String {
    return if (ignoreZero && this == 0F) "" else numberFormat(decimalDigits, false, this) + unit
}

/**
 * 格式化小数。
 * @param ignoreZero 是否忽略0，如果忽略当数值为0是返回空的字符串。
 * @param unit 单位。
 * @return 返回格式化后数字字符串。
 */
fun Double.formatToReal(ignoreZero: Boolean = false, decimalDigits: Int = -1, unit: String = ""): String {
    return if (ignoreZero && this == 0.0) "" else numberFormat(decimalDigits, false, this) + unit
}

/**
 * 格式化小数。
 * @param ignoreZero 是否忽略0，如果忽略当数值为0是返回空的字符串。
 * @param unit 单位。
 * @return 返回格式化后数字字符串。
 */
fun Double.formatToReal(ignoreZero: Boolean = false, unit: String = ""): String {
    return if (ignoreZero && this == 0.0) "" else numberFormat(-1, false, this) + unit
}

/**
 * 格式化金额。该方法会默认会除以100。
 * @param unit 单位。
 * @return 返回格式化后数字字符串。
 */
fun Long.formatPriceToReal(unit: String = ""): String {
    return numberFormat(-1, false, this / 100.0) + unit
}

/**
 * 格式化小数。
 * @param unit 单位。
 * @param dollarFormat 是否要格式为美元样式(千位分隔样式)，例如：135,435,231.09；可以不传，默认为true。
 * @param ignoreZero 是否忽略0，如果忽略当数值为0是返回空的字符串。
 * @return 返回格式化后数字字符串。
 */
fun Double.formatToRealDollar(unit: String = "", dollarFormat: Boolean = true, ignoreZero: Boolean = true): String {
    return if (ignoreZero && this == 0.0) "" else numberFormat(-1, dollarFormat, this) + unit
}

fun Long.formatToDollar(unit: String = "元"): String {
    return DecimalFormat(",###").format(this) + unit
}

/**
 * 将小数格式为为时间格式。
 */
fun Long.formatToTime(): String {
    return (this / 1000F).let { "${formatNumber(it / 3600)}:${formatNumber(it / 60 % 60)}:${formatNumber(it % 60)}" }
}

@Suppress("SimpleDateFormat")
fun Long?.formatDate(format: String = DateHelper.YYYY_MM_DD, defString: String? = null): String? {
    return if (this == null || this == 0L) {
        defString
    } else {
        val f = SimpleDateFormat(format)
        f.timeZone = TimeZone.getTimeZone("GMT+8")
        f.format(Date(this))
    }
}

/**
 * 获取两个时间的时间差的可读文本。
 * @param start 开始时间，较早的时间。
 * @param end 结束时间，较晚的时间。
 */
fun getReadableTimeSpace(start: Long, end: Long): String? {
    return if (end - start < 61000) {
        if (start >= end) {
            null
        } else {
            "${(end - start) / 1000}秒"
        }
    } else {
        val duration = (end - start) / 60000  //计算分钟数
        val days = duration / 1440
        val hours = duration % 1440 / 60
        val minute = duration % 86400
        if (duration == 0L) null else (if (days > 0) "${days}天" else "") + (if (hours > 0) "${hours}小时" else "") + (if (minute > 0) "${minute}分" else "")
    }
}

private fun formatNumber(number: Number): String {
    return String.format("%02d", number.toInt())
}

private fun numberFormat(decimalDigits: Int, dollarFormat: Boolean, number: Double): String {
    return if (decimalDigits > 0) {
        String.format("%${if (dollarFormat) "," else ""}.${decimalDigits}f", number)
    } else {
        DecimalFormat(if (decimalDigits < 0) "${if (dollarFormat) ",###" else "0"}.###" else if (dollarFormat) ",###" else "#").format(number)
    }
}

private fun numberFormat(decimalDigits: Int, dollarFormat: Boolean, number: Float): String {
    return if (decimalDigits > 0) {
        String.format("%${if (dollarFormat) "," else ""}.${decimalDigits}f", number)
    } else {
        DecimalFormat(if (decimalDigits < 0) "${if (dollarFormat) ",###" else "0"}.###" else if (dollarFormat) ",###" else "#").format(number)
    }
}

fun Long?.toNoNull(): Long {
    return this ?: 0
}

fun Int?.toNoNull(): Int {
    return this ?: 0
}