package com.dqc.todo.utils

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

fun Context.minute2HourAndMinute(minute: Int): String {
    if (minute < 60) {
        return "${minute}分钟"
    } else if (minute % 60 == 0){
        return "${minute / 60}小时"
    } else {
        return "${minute / 60}小时${minute % 60}分钟"
    }
}

fun Context.allSecond2HourAndMinute(second: Int): String {
    var hour = second / 60 / 60
    var minute = second / 60 % 60
    var second = second % 60

    return "${if (hour > 0) {"${hour}小时"} else {""} }" +
            "${if(minute > 0) {"${minute}分钟"} else {""} }" +
            "${second}秒"
}