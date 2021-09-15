package com.dqc.puppymoney.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.dqc.puppymoney.R


class BitmapDemo(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var bitmap = getBitmapFromDrawable(context, R.drawable.complete_ic_black)
        canvas?.drawBitmap(bitmap!!, width/2f - bitmap.width / 2, height/2f - bitmap.height / 2, null)
    }
    fun getBitmapFromDrawable(context: Context?, @DrawableRes drawableId: Int): Bitmap? {
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
}