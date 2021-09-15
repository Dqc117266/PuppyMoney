package com.dqc.puppymoney.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.dqc.puppymoney.R
import com.dqc.puppymoney.util.DisplayUtil
import com.dqc.puppymoney.util.dp2px
import java.util.*

class AnnualScheduleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mWidth = 0
    private var mHeight= 0
    private var mCenterX = 0
    private var mCenterY = 0
    private var mTheSunBitmap: Bitmap? = null
    private var mTheEarthBitmap: Bitmap? = null
    private var mCircleLineWidth = dp2px(3)
    private var mCircleRadius = 0
    private var mTheEarthPadding = dp2px(4)

    private var mTheSunRotateValue = 0f
    private var mAlphValue = 0f
    private var mIsShowText = false
    private var mIsDownView = false

    private var mRotateAnimator: ValueAnimator? = null

    private var mCircleLinePaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = mCircleLineWidth.toFloat()
        color = 0x99BCBCBC.toInt()
    }

    private var mProgressBarPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = mCircleLineWidth.toFloat()
        color = 0xffffffff.toInt()
        strokeCap = Paint.Cap.ROUND
    }

    private var mBitmapPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private var mTextPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = 0xffffffff.toInt()
        style = Paint.Style.FILL
        textSize = dp2px(18).toFloat()
    }

    private var mTipTextPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = 0xffffffff.toInt()
        style = Paint.Style.FILL
        textSize = dp2px(12).toFloat()
    }

    init {
        mTheSunBitmap = DisplayUtil.getBitmapFromDrawableId(context, R.drawable.the_sun_icon)
        mTheEarthBitmap = DisplayUtil.getBitmapFromDrawableId(context, R.drawable.the_earth_icon)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        /**
         * 组件测量
         * 布局里指定的size使用布局的size
         * 属性里指定的size使用属性的size
         * 都没有指定，使用默认size
         */
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = 0
        if (widthMode == MeasureSpec.EXACTLY) {
            widthSize = MeasureSpec.getSize(widthMeasureSpec)
            mWidth = widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mWidth
        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = 0
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = MeasureSpec.getSize(heightMeasureSpec)
            mHeight = heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mHeight
        }

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCircleRadius = (width - mCircleLineWidth)  / 2
        mCenterX = w / 2
        mCenterY = h / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBackground(canvas)
    }

    private fun drawText(canvas: Canvas?, yearMaxDay: Int, useDay: Int) {
        var str = "$useDay/$yearMaxDay"
        val centerTextLocation = getCenterTextLocation(str, mTextPaint)
        canvas?.drawText(str, centerTextLocation[0], centerTextLocation[1] - dp2px(8), mTextPaint)

        str = "剩余${yearMaxDay - useDay}天"
        val centerText = getCenterTextLocation(str, mTipTextPaint)
        canvas?.drawText(str, centerText[0], centerText[1] + dp2px(16), mTipTextPaint)
        Log.d("DrawText", " " + str)
    }

    private fun getCenterTextLocation(text: String, paint: Paint): FloatArray {
        val measureText = paint.measureText(text)
        val fm = paint.fontMetricsInt
        var startX = mCenterX - measureText / 2
        var startY = mCenterY - fm.descent + (fm.bottom - fm.top) / 2f
        return floatArrayOf(startX, startY)
    }

    private fun drawBackground(canvas: Canvas?) {
        canvas?.drawCircle(mCenterX.toFloat(), mCenterY.toFloat(), mCircleRadius.toFloat(), mCircleLinePaint)
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val maximum = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR)

        var rectf = RectF(mCenterX - mCircleRadius.toFloat(), mCenterY - mCircleRadius.toFloat(), mCenterX + mCircleRadius.toFloat(), mCenterY + mCircleRadius.toFloat())
        canvas?.drawArc(rectf, -90f, dayOfYear / maximum.toFloat() * 360, false, mProgressBarPaint)
        if (mIsShowText) {
            drawText(canvas, maximum, dayOfYear)
        } else {
            canvas?.save()
            canvas?.rotate(mTheSunRotateValue, mCenterX.toFloat(), mCenterY.toFloat())
            canvas?.drawBitmap(mTheSunBitmap!!, mCenterX - mTheSunBitmap!!.width / 2f, mCenterY - mTheSunBitmap!!.height / 2f, mBitmapPaint)
            canvas?.restore()

            canvas?.save()
            canvas?.rotate(dayOfYear / maximum.toFloat() * 360, mCenterX.toFloat(), mCenterY.toFloat())
            rotateEarth(canvas)
            canvas?.restore()
        }
    }

    private fun rotateEarth(canvas: Canvas?) {
        canvas?.save()
        canvas?.rotate(mTheSunRotateValue, mCenterX.toFloat(), mCenterY - mCircleRadius.toFloat() + mTheEarthPadding + mTheEarthBitmap!!.height / 2)
        canvas?.drawBitmap(mTheEarthBitmap!!, mCenterX - mTheEarthBitmap!!.width / 2f, mCenterY - mCircleRadius.toFloat() + mTheEarthPadding, mBitmapPaint)
        canvas?.restore()
    }

    fun startTheSunRotateAnim() {
        mRotateAnimator = ValueAnimator.ofFloat(360f)
        mRotateAnimator?.setDuration(12 * 1000)
        mRotateAnimator?.repeatCount = ValueAnimator.INFINITE
        mRotateAnimator?.interpolator = LinearInterpolator()
        mRotateAnimator?.addUpdateListener {
            mTheSunRotateValue = it.animatedValue as Float
//            Log.d("GASASDASD", "  " + mTheSunRotateValue)
            invalidate()
        }
        mRotateAnimator?.start()
    }

    fun pauseTheSunRotateAnim() {
        if (mRotateAnimator != null && mRotateAnimator!!.isStarted) {
            mRotateAnimator?.cancel()
            mRotateAnimator = null
        }
    }

    private fun startAlphaAnim(isShow: Boolean) {
        val alpha = ValueAnimator.ofFloat(1f)
        alpha.setDuration(400)
        alpha.interpolator = LinearInterpolator()
        alpha.addUpdateListener {
            mAlphValue = it.animatedValue as Float
            Log.d("mAlphValue", "  " + mAlphValue)
            if (!isShow) {
                mAlphValue = (1 - mAlphValue)
            }

            mTextPaint.alpha = (255f * mAlphValue).toInt()
            mTipTextPaint.alpha = (255f * mAlphValue).toInt()
            invalidate()
        }

        alpha.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (!isShow) {
                    mIsShowText = false
                }
                //mIsShowText = false
            }
        })
        alpha.start()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        Log.d("Touch", " touch")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsDownView = true
            }

            MotionEvent.ACTION_UP -> {
                if (mIsDownView && isViewTouchRange(this, event?.rawX.toInt(), event?.rawY.toInt())) {
                    if (!mIsShowText) {
                        mIsShowText = true
                        startAlphaAnim(true)
                    } else {
                        startAlphaAnim(false)
                    }
                }
                Log.d("Touch", " touch " +mIsDownView + " " + mIsShowText)
                mIsDownView = false
            }

            MotionEvent.ACTION_CANCEL -> {
                mIsDownView = false
            }
        }

        return true
    }

    fun isViewTouchRange(view: View, x: Int, y:Int):Boolean {
        val intArray = IntArray(2)
        view.getLocationInWindow(intArray)
        var left = intArray[0]
        var top = intArray[1]
        var right = left + width
        var bottom = top + height
        return (x >= left && x <= right && y >= top && y <= bottom)
    }
}