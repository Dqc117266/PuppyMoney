package com.dqc.puppymoney.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.util.Log
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.dqc.puppymoney.R
import com.dqc.puppymoney.util.DisplayUtil
import com.dqc.puppymoney.util.dp2px

class DotView(context: Context?) : View(context) {

    private val WIDTH_AND_HEIGHT: Int = DisplayUtil.dip2px(context!!, 18).toInt()
    private var mIsSelected: Boolean = false
    private var mRoundBoxPaint: Paint = Paint().apply {
        color = 0xff262626.toInt()
        style = Paint.Style.STROKE
        isAntiAlias = true
        isDither = true
        strokeWidth = DisplayUtil.dip2px(context!!, 1)
    }

    private var mScaleAnimator: ValueAnimator ?= null
    private var mValue :Float = 1f
    private var mIsExistText: Boolean = false
    private var mBitmap: Bitmap ?= null
    private var mBitmapWidth = DisplayUtil.dip2px(context!!, 1) * 6.39
    private var mBitmapHeight = DisplayUtil.dip2px(context!!, 1) * 5.47
//    private var bitmap: Bitmap

    private var mRoundPointPaint: Paint = Paint().apply {
        color = 0xff262626.toInt()
        style = Paint.Style.FILL
        isAntiAlias = true
        isDither = true
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        mBitmap = getBitmapFromDrawable(context, R.drawable.complete_ic_black)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawPoint(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(WIDTH_AND_HEIGHT, WIDTH_AND_HEIGHT)
    }

    fun drawPoint(canvas: Canvas?) {
        canvas?.drawCircle(width / 2f, height / 2f, WIDTH_AND_HEIGHT / 2f - (DisplayUtil.dip2px(context, 1) / 2), mRoundBoxPaint)
        if (mIsSelected) {
            canvas?.drawCircle(width / 2f, height / 2f, DisplayUtil.dip2px(context, 4) * mValue, mRoundPointPaint)
        }

        if (mIsExistText && !mIsSelected) {
            canvas?.drawBitmap(mBitmap!!, (width / 2 - mBitmapWidth / 2).toFloat() * mValue, (height / 2f - mBitmapHeight / 2).toFloat() * mValue, null)
        }
    }

    fun setIsSelected(isSelected: Boolean) {
        mIsSelected = isSelected
        scalePointAnimator()
    }

    fun setIsExistText(isExistText: Boolean) {
        mIsExistText = isExistText
        scalePointAnimator()
    }

    fun getIsSelected() : Boolean {
        return mIsSelected
    }

    fun scalePointAnimator() {
        mScaleAnimator = ValueAnimator.ofFloat(1f)
        mScaleAnimator?.duration = 200
        mScaleAnimator?.interpolator = AnticipateOvershootInterpolator()
        mScaleAnimator?.addUpdateListener {
            mValue = it.animatedValue as Float
            invalidate()
        }
        mScaleAnimator?.start()
    }

    fun getBitmapFromDrawable(context: Context?, @DrawableRes drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context!!, drawableId)
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is VectorDrawable || drawable is VectorDrawableCompat) {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, mBitmapWidth.toInt(), mBitmapHeight.toInt())
            drawable.draw(canvas)
            bitmap
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }
}