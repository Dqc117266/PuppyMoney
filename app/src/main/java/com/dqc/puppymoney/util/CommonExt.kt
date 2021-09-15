package com.dqc.puppymoney.util

import android.content.Context
import android.view.View

/**
 * dp值转为px
 */

fun Context.dp2px(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

/**
 * px值转为dp
 */
fun Context.px2dp(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp / scale + 0.5f).toInt()
}

/**
 * dp值转为px
 */

fun View.dp2px(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

/**
 * px值转为dp
 */
fun View.px2dp(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp / scale + 0.5f).toInt()
}

fun View.sp2px(sp: Int) :Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (fontScale * fontScale + 0.5f).toInt()
}