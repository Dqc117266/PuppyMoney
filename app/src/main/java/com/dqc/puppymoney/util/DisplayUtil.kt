package com.dqc.puppymoney.util

import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.media.ExifInterface
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.dqc.puppymoney.bean.DateBean
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


/**
 * Created with Android Studio.
 * Description:
 * @author: Wangjianxian
 * @CreateDate: 2020/6/14 21:26
 */
object DisplayUtil {

    fun px2dip(context: Context, pxValue: Int): Float {
        val scale: Float = context.getResources().getDisplayMetrics().density
        return (pxValue / scale + 0.5f).toFloat()
    }

    fun dip2px(context: Context, dipValue: Int): Float {
        val scale: Float = context.getResources().getDisplayMetrics().density
        return (dipValue * scale + 0.5f).toFloat()
    }

    fun px2sp(context: Context, pxValue: Float): Float {
        val fontScale: Float = context.getResources().getDisplayMetrics().scaledDensity
        return (pxValue / fontScale + 0.5f).toFloat()
    }

    fun sp2px(context: Context, spValue: Int): Float {
        val fontScale: Float = context.getResources().getDisplayMetrics().scaledDensity
        return (spValue * fontScale + 0.5f).toFloat()
    }

    fun getScreenWidth(context: Context): Float {
        val dm: DisplayMetrics = context.getResources().getDisplayMetrics()
        return dm.widthPixels.toFloat()
    }

    fun getScreenHeight(context: Context): Float {
        val dm: DisplayMetrics = context.getResources().getDisplayMetrics()
        return dm.heightPixels.toFloat()
    }

    fun getBitmapFromDrawableId(context: Context?, @DrawableRes drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context!!, drawableId)
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is VectorDrawable || drawable is VectorDrawableCompat) {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }

    fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is VectorDrawable || drawable is VectorDrawableCompat) {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }

    fun monthSimple(month: Int):String {
        val arrayOf = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul" ,"Aug", "Sep", "Oct", "Nov", "Dec")
        return arrayOf.get(month)
    }

    fun weekDayChinese(week: Int):String {
        val arrayOf = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
        return arrayOf.get(week - 1)
    }

    fun numberAddZero(num: Int): String {
        if (num < 10)
            return "0$num"
        return "$num"
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun byteArrayToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size);
    }

    fun setViewVisibility(view: View, visibility: Int) {
        if (visibility == View.VISIBLE) {
            if (view.visibility == View.GONE) {
                view.visibility = View.VISIBLE
            }
        }

        if (visibility == View.GONE) {
            if (view.visibility == View.VISIBLE) {
                view.visibility = View.GONE
            }
        }
    }

    fun rotateBitmapByDegree(bitmap: Bitmap, degree: Float): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(degree)

        var result: Bitmap

        try {
            result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            result = bitmap
        }

        if (bitmap != result) {
            bitmap.recycle()
        }

        return result
    }

    fun getOrientationRotate(path: String): Float {
        var degree = 0

        try {
            var exifInterface = ExifInterface(path)
            var orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            when(orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    degree = 90
                }

                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    degree = 180
                }

                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    degree = 270
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree.toFloat()
    }

    /*数字转成中文
   *只适合显示 0 - 99999的整数
   */
    fun numToChinese(num: Int):String {
        val chineseNum = getChineseNum()
        if (num == 0) {
            return "${chineseNum[num]}"
        }
        val arrayList = arrayListOf<Int>()
        var n = num
        while (n > 0) {
            arrayList.add(n % 10)
            n /= 10
        }
        var sb = StringBuffer()
        for (i in arrayList.size - 1 downTo 0) {
            if (nextIsNotZero(i, arrayList) || (arrayList[i] != 0)) {
                if (i == 0 || nextIsNotZero(i, arrayList)) {
                    sb.append(chineseNum[arrayList[i]])
                } else {
                    sb.append(chineseNum[arrayList[i]]).append(getBit(i))
                }
            }
        }
        return sb.toString()
    }

    //当前位置是零下一位置不是零的情况
    private fun nextIsNotZero(curIndex: Int, arrayList: ArrayList<Int>) :Boolean {

        return arrayList[curIndex] == 0
                && curIndex > 0
                && arrayList[curIndex - 1] != 0
    }
    private fun getBit(index: Int) :Char{
        val charArray = charArrayOf('个', '十', '百', '千', '万')
        if (index > charArray.size - 1) {
            return charArray[index % charArray.size - 1]
        }
        return charArray[index]
    }

    private fun getChineseNum():CharArray {
        return charArrayOf('零',
                '一', '二', '三', '四', '五',
                '六', '七', '八', '九')
    }

    fun getCurrentTime(): DateBean {

        var calendar = Calendar.getInstance()

        return DateBean(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.DAY_OF_WEEK),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND))
    }

    fun millsToMinute(mills: Int): Int {
        return mills / 1000 / 60
    }

    fun millsToSecond(mills: Int): Int {
        return mills / 1000 % 60
    }

}