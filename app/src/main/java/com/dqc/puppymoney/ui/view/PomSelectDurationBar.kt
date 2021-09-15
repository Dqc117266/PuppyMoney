package com.dqc.puppymoney.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.dqc.puppymoney.interfaces.IPomCountCallBack
import com.dqc.puppymoney.interfaces.IPomDartionCallBack
import com.dqc.puppymoney.util.dp2px

class PomSelectDurationBar(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mCircleRadius = dp2px(5)
    private var mCirclePadding = dp2px(32)
    private var mPaddingTopAndBottom = dp2px(10)
    private var mMaxSelectCount = 7
    private var mViewLocalX: IntArray = IntArray(mMaxSelectCount)
    private var mSelectCount = 3
    private var mIsDownPress = false
    private var mIPomDartionCallBack: IPomDartionCallBack? = null

    private var mUnSelectPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        strokeWidth = dp2px(3).toFloat()
        color = 0xffbcbcbc.toInt()
    }

    private var mSelectPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        strokeWidth = dp2px(3).toFloat()
        color = 0xffff6666.toInt()
    }

    fun setIPomDartionCallBack(pomDartionCallBack: IPomDartionCallBack) {
        mIPomDartionCallBack = pomDartionCallBack
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = (mCircleRadius * 2) * mMaxSelectCount + mCirclePadding * (mMaxSelectCount - 1)
        setMeasuredDimension(width, mCircleRadius * 2 + mPaddingTopAndBottom * 2)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawProgressBar(canvas)
    }

    private fun drawProgressBar(canvas: Canvas?) {
        var x = mCircleRadius
        for (i in 0..mSelectCount) {
            canvas?.drawCircle(x.toFloat(), height / 2f, mCircleRadius.toFloat(), mSelectPaint)
            if (i != mSelectCount) {
                canvas?.drawLine(x.toFloat(), height / 2f, (x + mCirclePadding + mCircleRadius).toFloat(), height / 2f, mSelectPaint)
            }
            x += (mCircleRadius * 2) + mCirclePadding
        }
    }

    private fun drawBackground(canvas: Canvas?) {
        canvas?.drawLine(mCircleRadius.toFloat(), height / 2f, (width - mCircleRadius).toFloat(), height / 2f, mUnSelectPaint)
        var x = mCircleRadius
        for (i in 0..mMaxSelectCount - 1) {
            canvas?.drawCircle(x.toFloat(), height / 2f, mCircleRadius.toFloat(), mUnSelectPaint)
            mViewLocalX[i] = x - mCircleRadius
            x += (mCircleRadius * 2) + mCirclePadding
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsDownPress = true
            }

            MotionEvent.ACTION_MOVE -> {
                if (getTouchLocationCount(event.x.toInt()) != -1) {
                    mSelectCount = getTouchLocationCount(event.x.toInt())
                    if (mIPomDartionCallBack != null) {
                        mIPomDartionCallBack?.onDartion(mSelectCount)
                    }
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (mIsDownPress) {
                    if (getTouchLocationCount(event.x.toInt()) != -1) {
                        mSelectCount = getTouchLocationCount(event.x.toInt())
                        if (mIPomDartionCallBack != null) {
                            mIPomDartionCallBack?.onDartion(mSelectCount)
                        }
                    }
                    invalidate()
                }
            }

        }
        return true
    }

    private fun getTouchLocationCount(x: Int):Int {
        var count = -1
        for (i in 0..mViewLocalX.size - 1) {
            if (mViewLocalX[i] <= x) {
                count = i
            }
        }
        return count
    }

    fun getSelelctCount(): Int {
        return mSelectCount
    }

}