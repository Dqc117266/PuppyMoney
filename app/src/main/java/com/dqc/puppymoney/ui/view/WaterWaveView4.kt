package com.dqc.puppymoney.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.dqc.puppymoney.util.dp2px

class WaterWaveView4(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mCenterX = 0
    private var mCenterY = 0
    private var mMinute = 25
    private var mCurMills = 5 * 60 * 1000
    private var mRadius = 0f
    var mWaterWaveAnimator: ValueAnimator? = null
    private var mAnimValue = 0f;
    private var mNeedStopAnimator = false

    private var mProgressBgPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        strokeWidth = dp2px(5).toFloat() / 2
        style = Paint.Style.STROKE
        color = 0x33ffffff
    }

    private var mProgressPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        strokeWidth = dp2px(5).toFloat() / 2
        style = Paint.Style.STROKE
        color = 0xCCffffff.toInt()
        strokeCap = Paint.Cap.ROUND
    }

    private var mWavesPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        strokeWidth = dp2px(2).toFloat()
        style = Paint.Style.STROKE
        color = 0x7fffffff.toInt()
        strokeCap = Paint.Cap.ROUND
    }

    fun setMinute(minute: Int) {
        mMinute = minute
        mCurMills = mMinute * 1000 * 60
        invalidate()
    }

    fun setCurMills(mills: Int) {
        mCurMills = mills
        invalidate()
    }

    init {
        initWaterWaveAnim()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2
        mCenterY = h / 2

        if (w >= h) {
            mRadius = h / 2 * 3 / 4f
        } else {
            mRadius = w / 2 * 3 / 4f
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawWave(canvas)
        drawProgressBar(canvas)
    }

    private fun drawWave(canvas: Canvas?) {
        if (mAnimValue > 0.01f) {
            canvas?.drawCircle(mCenterX.toFloat(), mCenterY.toFloat(), mRadius + dp2px(2) + (mCenterX - mRadius) * mAnimValue, mWavesPaint)
        }
    }

    private fun drawProgressBar(canvas: Canvas?) {
        canvas?.drawCircle(mCenterX.toFloat(), mCenterY.toFloat(), mRadius, mProgressBgPaint)

        var rect = RectF(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius)
        var range = mCurMills / (mMinute * 60 * 1000f)
        Log.d("WaterVave", " range " + range + " mCurMills " + mCurMills)
        canvas?.drawArc(rect, -90f + 360 - (range * 360f), 360 - (360 - range * 360f),false, mProgressPaint)
    }

    fun initWaterWaveAnim() {
        mWaterWaveAnimator = ValueAnimator.ofFloat(0f, 1f)
        mWaterWaveAnimator?.setDuration(4000)
        mWaterWaveAnimator?.interpolator = DecelerateInterpolator()
        mWaterWaveAnimator?.repeatCount = ValueAnimator.INFINITE
        mWaterWaveAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator?) {
                Log.d("NEEDSTOP", " " + mNeedStopAnimator)
                if (mNeedStopAnimator) {
                    mNeedStopAnimator = false
                    mWaterWaveAnimator?.cancel()
                }
            }

        })
        mWaterWaveAnimator?.addUpdateListener {
            mAnimValue = it.animatedValue as Float
            mWavesPaint!!.alpha = (255 * 3 / 4 * (1 - mAnimValue)).toInt()
            invalidate()
        }
    }

    fun startWaterWaveAnim() {
        mNeedStopAnimator = false
        if (mWaterWaveAnimator != null && !mWaterWaveAnimator?.isStarted!!) {
            mWaterWaveAnimator?.start()
        }
    }

    fun pauseWaterWaveAnim() {
        if (mWaterWaveAnimator != null) {
            mNeedStopAnimator = true
        }
    }

}