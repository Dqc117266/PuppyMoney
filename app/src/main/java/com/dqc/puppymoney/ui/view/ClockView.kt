package com.dqc.puppymoney.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.dqc.puppymoney.util.dp2px
import java.util.*

class ClockView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mCenterX = 0f
    private var mCenterY = 0f

    private var mClockRadius = dp2px(120)
    private var mHoursMarkLength = dp2px(25)
    private var mMinuteMarkLength = dp2px(15)
    private var mSecondPonterLength = dp2px(94)
    private var mMinutePonterLength = dp2px(94)
    private var mHoursPonterLength = dp2px(50)


    private var mCalender: Calendar? = null

    private var mMisTime = 0f
    private var mSecondTime = 0f
    private var mMinuteTime = 0f
    private var mHoursTime = 0f


    private var mHourMarkPaint = Paint().apply {
        color = 0xffffffff.toInt()
        isDither = true
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = dp2px(6).toFloat()
     }

    private var mMinuteMarkPaint = Paint().apply {
        color = 0x7fffffff
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = dp2px(6).toFloat()
    }

    private var mSecondPointerPaint = Paint().apply {
        color = 0xFFFF1512.toInt()
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = dp2px(4).toFloat()
    }

    private var mMinutePointerPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = dp2px(6).toFloat()
    }

    private var mHourPointerPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = dp2px(9).toFloat()
    }

    private var mNumberPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        textSize = dp2px(20).toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2f
        mCenterY = h / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(Color.parseColor("#1F7AC9"))
        drawClockBackground(canvas)
        drawClock(canvas)

        invalidate()
    }

    private fun drawClock(canvas: Canvas?) {
        refreCurrentTime()

        var secondAngle1 = mSecondTime / 60 * 360
        var secondAngle = (mSecondTime * 1000f + mMisTime) / (60 * 1000f) * 360
        var minuteAngle = (mMinuteTime * 60 + mSecondTime) / (60 * 60) * 360
        var hoursAngle = (mHoursTime * 60 + mMinuteTime) / (12 * 60) * 360

        Log.d("ClockView", " secAngle ${secondAngle1} min ${minuteAngle} hou${mHoursTime}")

//        for (i in 1..12) {
//            val centerTextLocation = getCenterTextLocation("$i", mNumberPaint)
//            val rotateLocation = rotateLocation((30 * (i)).toFloat() - 90, mCenterX, mCenterY + 10, mClockRadius.toFloat() - 70)
//            canvas?.drawText("$i", rotateLocation[0] - centerTextLocation[0], rotateLocation[1], mNumberPaint)
//        }

        canvas?.save()
        canvas?.rotate(hoursAngle, mCenterX, mCenterY)
        canvas?.drawLine(mCenterX, mCenterY + 20, mCenterX, mCenterY - mHoursPonterLength, mHourPointerPaint)
        canvas?.restore()

        canvas?.save()
        canvas?.rotate(minuteAngle, mCenterX ,mCenterY)
        canvas?.drawLine(mCenterX, mCenterY + 20, mCenterX, mCenterY - mMinutePonterLength, mMinutePointerPaint)
        canvas?.restore()

        canvas?.save()
        canvas?.rotate(secondAngle1, mCenterX, mCenterY)
        canvas?.drawLine(mCenterX, mCenterY + 20, mCenterX, mCenterY - mSecondPonterLength, mSecondPointerPaint)
        canvas?.restore()


    }

    private fun getCenterTextLocation(text: String, paint: Paint): FloatArray {
        val measureText = paint.measureText(text)
        val fm = paint.fontMetricsInt
        var startX = measureText / 2
        var startY = fm.descent + (fm.bottom - fm.top) / 2f
        return floatArrayOf(startX, startY)
    }

    private fun rotateLocation(rotateAngle: Float, centerX: Float, centerY: Float, radius: Float): FloatArray {
        var rotateLocation = FloatArray(2)
        var sin = Math.sin(rotateAngle / 180f * Math.PI)
        var cos = Math.cos(rotateAngle / 180f * Math.PI)
        rotateLocation[0] = centerX + (radius * cos).toFloat()
        rotateLocation[1] = centerY + (radius * sin).toFloat()
//        Log.d("Pomodoro", " location 1 " + m)
        return rotateLocation
    }

    private fun refreCurrentTime() {
        mCalender = Calendar.getInstance()

        mMisTime = mCalender?.get(Calendar.MILLISECOND)?.toFloat()!!
        mSecondTime = mCalender?.get(Calendar.SECOND)?.toFloat()!!
        mMinuteTime = mCalender?.get(Calendar.MINUTE)?.toFloat()!!
        mHoursTime = mCalender?.get(Calendar.HOUR_OF_DAY)?.toFloat()!!
    }

    private fun drawClockBackground(canvas: Canvas?) {

        for (i in 0..59) {
            canvas?.save()
            var angle = 6f * i
            canvas?.rotate(angle, mCenterX, mCenterY)

            if (angle % 30 == 0f) {
                canvas?.drawLine(mCenterX, mCenterY - mClockRadius + mHoursMarkLength, mCenterX, mCenterY - mClockRadius, mHourMarkPaint)
            } else {
                canvas?.drawLine(mCenterX, mCenterY - mClockRadius + mMinuteMarkLength, mCenterX, mCenterY - mClockRadius, mMinuteMarkPaint)
            }
            canvas?.restore()
        }
    }
}