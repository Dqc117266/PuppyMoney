package com.dqc.puppymoney.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import com.dqc.puppymoney.util.dp2px

class AView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mCenterX = 0
    private var mCenterY = 0
    private var mMinute = 25
    private var mRestMinute = 5
    private var mRadius = dp2px(120)
    private var mMinutePointLength = dp2px(32)
    private var mPomodoroPointLength = dp2px(60)
    private var mStartMillisSecond: Long = 0L
    private var mCurrentMinute = 0
    private var mCurrentSecond = 0
    private var mWorkColor = 0xffff6666
    private var mRestColor = 0xff0066cc

    private var mWorkBackgroundPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        color = 0xffff6666.toInt()
    }

    private var mRestBackgroundPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        color = 0xff0066cc.toInt()
    }

    private var mMinutePaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
//        strokeWidth = dp2px(6).toFloat()
        color = 0xffA1A1A1.toInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2
        mCenterY = h / 2
    }

    private var mPomodoroPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = dp2px(8).toFloat()
        color = 0xffffffff.toInt()
    }

    init {
        mStartMillisSecond = SystemClock.elapsedRealtime()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawClock(canvas)
    }

    private fun getClockRect(radius: Int):RectF {
        return RectF((mCenterX - radius).toFloat(),
                (mCenterY - radius).toFloat(), (mCenterX + radius).toFloat(), (mCenterY + radius).toFloat()
        )
    }

    private fun drawClock(canvas: Canvas?) {
        var allTime = (mMinute + mRestMinute) * 60 * 1000
        var passTime = SystemClock.elapsedRealtime() - mStartMillisSecond

        mCurrentMinute = ((allTime - passTime) / 60).toInt()
        mCurrentSecond = ((allTime - passTime) / 60 / 1000).toInt()

        canvas?.save()
        canvas?.rotate(((passTime) % 60000).toFloat() / 60000 * 360, mCenterX.toFloat(), mCenterY.toFloat())
//        canvas?.drawLine(mCenterX.toFloat(), (mCenterY).toFloat(), mCenterX.toFloat(), (mCenterY - mMinutePointLength).toFloat(), mMinutePaint)
        canvas?.drawCircle(mCenterX.toFloat(), mCenterY - mRadius - 60f, 30f, mWorkBackgroundPaint)
        canvas?.restore()

        canvas?.save()
        canvas?.rotate(passTime.toFloat() / allTime * 360f, mCenterX.toFloat(), mCenterY.toFloat())
        canvas?.drawLine(mCenterX.toFloat(), (mCenterY).toFloat() + 30, mCenterX.toFloat(), (mCenterY - mPomodoroPointLength).toFloat(), mPomodoroPaint)
        canvas?.restore()

        invalidate()
    }

    private fun drawBackground(canvas: Canvas?) {
//        var rect = RectF((mCenterX - mRadius).toFloat(), (mCenterY + mRadius).toFloat(), (mCenterX + mRadius).toFloat(), (mCenterY - mRadius).toFloat())
        val clockRect = getClockRect(mRadius)

        var workAngle = mMinute / (mMinute.toFloat() + mRestMinute) * 360
        canvas?.drawArc(clockRect, -90f, workAngle, true, mWorkBackgroundPaint)

        var restAngle = mRestMinute / (mMinute.toFloat() + mRestMinute) * 360
        canvas?.drawArc(clockRect, workAngle - 90, restAngle, true, mRestBackgroundPaint)

        for (i in 0..mMinute + mRestMinute - 1) {
            canvas?.save()
            canvas?.rotate(360f / mMinute * i, mCenterX.toFloat(), mCenterY.toFloat())
//            canvas?.drawLine(mCenterX.toFloat(), mCenterY - mRadius + 60f, mCenterX.toFloat(), mCenterY - mRadius.toFloat(), mMinutePaint)
            canvas?.drawCircle(mCenterX.toFloat(), (mCenterY - mRadius).toFloat(), 30f, mMinutePaint)
            canvas?.restore()
        }
    }


}