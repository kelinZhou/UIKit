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
import java.io.File
import java.io.FileOutputStream

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