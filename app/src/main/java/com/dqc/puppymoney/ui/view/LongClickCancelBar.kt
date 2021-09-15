package com.dqc.puppymoney.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.dqc.puppymoney.util.dp2px

class LongClickCancelBar(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mWidth = dp2px(50)
    private var mHeight = dp2px(4)
    private var mStartMills = 0L
    private var mRiseAnimValue = 0f
    private var mRiseAnimtor: ValueAnimator? = null
    private var mOnLongClickCancel: OnLongClickCancel? = null

    var mPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        color = 0xffffffff.toInt()
        style = Paint.Style.STROKE
        strokeWidth = dp2px(4).toFloat()
        strokeCap = Paint.Cap.ROUND
    }

    var mBgPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        color = 0xcc777777.toInt()
        style = Paint.Style.STROKE
        strokeWidth = dp2px(4).toFloat()
        strokeCap = Paint.Cap.ROUND
    }

    fun setOnLongClickCancel(onLongClickCancel: OnLongClickCancel) {
        mOnLongClickCancel = onLongClickCancel
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawProgressBar(canvas)
    }

    fun setStartMills(mills: Long) {
        mStartMills = mills
    }

    private fun drawProgressBar(canvas: Canvas?) {
        if (mRiseAnimValue > 0) {
            canvas?.drawLine(dp2px(2).toFloat(), height / 2f, width.toFloat() - dp2px(2), height / 2f, mBgPaint)
            canvas?.drawLine(dp2px(2).toFloat(), height / 2f, (width * mRiseAnimValue) - dp2px(2), height / 2f, mPaint)
        }
    }

    fun startProgressRiseAnim() {
        if (mRiseAnimValue == 0f) {
            startAlphaAnimtor(true)
        }
        mRiseAnimtor = ValueAnimator.ofFloat(mRiseAnimValue, 1f)
        mRiseAnimtor?.setDuration(2000)
        mRiseAnimtor?.interpolator = LinearInterpolator()
        mRiseAnimtor?.addUpdateListener {
            mRiseAnimValue = it.animatedValue as Float
            Log.d("mRiseAnimtor", "update ${mRiseAnimValue}")
            if (mRiseAnimValue == 1f) {
                startAlphaAnimtor(false)
                mRiseAnimValue = 0f
                if (mOnLongClickCancel != null) {
                    mOnLongClickCancel?.onCancel()
                }
            }
            invalidate()
        }
        mRiseAnimtor?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                Log.d("mRiseAnimtor", "onEnd")
            }
        })
        mRiseAnimtor?.start()
    }

    fun startProgressFallAnim() {
        if (mRiseAnimValue < 1) {
            if (mRiseAnimtor != null) {
                mRiseAnimtor?.cancel()
                mRiseAnimtor = null
            }
            var anim = ValueAnimator.ofFloat(mRiseAnimValue, 0f)
            anim.setDuration(400)
            anim.interpolator = LinearInterpolator()
            anim.addUpdateListener {
                mRiseAnimValue = it.animatedValue as Float
                invalidate()
            }
            anim.start()
        }
    }

    fun startAlphaAnimtor(isShow: Boolean) {
        var startValue = 0f
        var endValue = 1f
        if (!isShow) {
            startValue = 1f
            endValue = 0f
        }

        var alpha = ValueAnimator.ofFloat(startValue, endValue)
        alpha.setDuration(200)
        alpha.addUpdateListener {
            var alphaValue = it.animatedValue as Float
            mPaint.alpha = (alphaValue * 255).toInt()
            mBgPaint.alpha = (alphaValue * 255).toInt()
            invalidate()
        }
        alpha.start()
    }

    interface OnLongClickCancel {
        fun onCancel()
    }

}