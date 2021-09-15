package com.dqc.puppymoney.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View

class FibonacciView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mRadius = 12f
    private var mCount = 0

    companion object {
        val DRAW_RIGHT = 0
        val DRAW_DOWN = 1
        val DRAW_LEFT = 2
        val DRAW_UP = 3
    }

    private var mPaint: Paint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = 0xff666666.toInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2f
        mCenterY = h / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        drawFibonacciView(canvas)
        drawFomaccoView(canvas)
    }

    private fun drawFomaccoView(canvas: Canvas?) {

        for (i in 0..3) {
            var offsetX = 0f
            var offsetY = 0f
            val fomaccoNum = getFomaccoNum(i)
            var sumRadius = mRadius * fomaccoNum
            Log.d("FibonacciView", " radius " + sumRadius + " fomaccoNum " + fomaccoNum)
            if (fomaccoNum > 1) {
                offsetX = sumRadius - (getFomaccoNum(i - 1) * mRadius)
                if (i > 2) {
                    offsetY = (getFomaccoNum(i - 1) * mRadius) - (getFomaccoNum(i - 2) * mRadius)
                }
            }
            drawPoint(canvas, mCenterX + offsetX, mCenterY - offsetY, sumRadius, i)
        }
    }

    private fun getFomaccoNum(n: Int): Int {
        if (n == 0 || n == 1) {
            return 1;
        } else {
            return getFomaccoNum(n - 1) + getFomaccoNum(n - 2)
        }
    }

    private fun drawPoint(canvas: Canvas?, centerX: Float, centerY: Float, radius: Float, index: Int) {
        var pointCount = 15
        var startAngle = 90.0
        var lastX = 0.0
        var lastY = 0.0
        val locationAngle = getLocationAngle(mCount % 4)
        mCount ++
        Log.d("LocationAngle", " location ${locationAngle} ")

        for (i in 0..pointCount) {
            var angle = 90f / pointCount * i
            var cos = Math.cos((angle + startAngle + locationAngle) / 180f * Math.PI)
            var sin = Math.sin((angle + startAngle + locationAngle) / 180f * Math.PI)
            var x = centerX + sin * radius
            var y = centerY + cos * radius
//            canvas?.drawPoint(x.toFloat(), y.toFloat(), mPaint)
            if (i > 0) {
                canvas?.drawLine(lastX.toFloat(), lastY.toFloat(), x.toFloat(), y.toFloat(), mPaint)
            }

            lastX = x
            lastY = y
        }
    }

    private fun getLocationAngle(location: Int): Int {
        var angle = 0

        when (location) {
            DRAW_RIGHT -> {
                angle = 0
            }

            DRAW_DOWN -> {
                angle = 90
            }

            DRAW_LEFT -> {
                angle = 180
            }

            DRAW_UP -> {
                angle = 270
            }
        }

        return angle
    }

}