package com.dqc.puppymoney.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.dqc.puppymoney.R
import com.dqc.puppymoney.util.DisplayUtil

class PomodoroView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mMinute: Int = 8
    private var mPomodoroRadius: Float = 0f
    private var mPomodoroMinuteRadius: Float = DisplayUtil.dip2px(context!!, 14)
    private var mPomodoroSecondRadius: Float = DisplayUtil.dip2px(context!!, 3)
    private var mSecondWithSecondOffset: Float = DisplayUtil.dip2px(context!!, 2)
    private var mMinuteWithMinuteOffset: Float = DisplayUtil.dip2px(context!!, 12)
    private var mAngle: Float = 0f
    private var mPomodoroBollRadius: Float = 0f
    private var mCenterX: Float = 0f
    private var mCenterY: Float = 0f
    private var mRotateLocation: FloatArray = FloatArray(2)
    private var mAnimatorRotateLocal: FloatArray = FloatArray(2)
    private var mStartAngleOffset: Int = 90
    private var mRotateRangle: Float = 0f
    private var mCurrentMilliSecond: Long = 0
    private var mOldCount = 0

    private var mMinPomodoroRadius: Float = DisplayUtil.dip2px(context!!, 100)

    private var mRotateAnimator: ValueAnimator? = null
    private var mScaleRange: Float = 2f

    private var mHideAnimValue: Float = 0f
    private var mIsStartHideAnim: Boolean = false
    private var mMinuteCircleChangeValue: Float = 0f
    private var mHideAnimHeight: Float = DisplayUtil.dip2px(context!!, 100)
    private var mHideAnimPaint: Paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        color = 0xffDCDCDC.toInt()
    }

    private var mCount = 0

    private var mMinutePaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        color = 0xffDCDCDC.toInt()
    }

    init {

        var ob = context?.obtainStyledAttributes(attrs, R.styleable.PomodoroView)
        mMinute = ob?.getDimensionPixelSize(R.styleable.PomodoroView_minute, 18)!!
//        updateRadius()
        mAngle = 360f / mMinute

        var allOffset = mPomodoroMinuteRadius + mMinuteWithMinuteOffset
        if (mMinute > 1) {
            mPomodoroRadius = allOffset / Math.sin((mAngle / 2f) / 180 * Math.PI).toFloat()
            if (mPomodoroRadius < mMinPomodoroRadius) {
                mPomodoroRadius = mMinPomodoroRadius
            }
        } else {
            mPomodoroRadius = 0f
        }
        Log.d("PomodoroView", " " + mPomodoroRadius.toInt() + " allOffset " + allOffset
                + "  " + Math.sin((mAngle / 2f) / 180 * Math.PI) + " " + (mAngle / 2f) / 180 * Math.PI / 2)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2f
        mCenterY = h / 2f

        mCurrentMilliSecond = SystemClock.elapsedRealtime()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        updateTime()
        drawMinuteCircle(canvas)
    }

    private fun updateTime() {
        var curTiming = SystemClock.elapsedRealtime() - mCurrentMilliSecond
        var countDown = mMinute * 1000 - curTiming
        Log.d("Pomodoro", " countDown :${countDown} $mIsStartHideAnim")
        if (countDown > 0) {
            Log.d("Pomodoro", " countDown :${mMinute - (countDown / 1000)}")

            mCount = (mMinute - ((countDown / 1000) + 1)).toInt()
            if (mOldCount != mCount) {
                viewChanged()
            }
            if (!mIsStartHideAnim) {
                invalidate()
            }
        }
        mOldCount = mCount
    }

    private fun drawMinuteCircle(canvas: Canvas?) {

        var mCenterCircleRadius = mPomodoroRadius - (mPomodoroMinuteRadius * 1.5f) - mMinuteWithMinuteOffset
        if (mCount == mMinute - 1) {

            val rotate = rotateLocation(-mStartAngleOffset.toFloat(), mCenterX, mCenterY, mPomodoroRadius)
            mHideAnimPaint.alpha = (1f - (mHideAnimValue / 1.5f) * 255).toInt()
            canvas?.drawCircle(rotate[0], rotate[1] - (mHideAnimHeight * mHideAnimValue), mPomodoroMinuteRadius * (1.5f - mHideAnimValue), mHideAnimPaint)

            canvas?.drawCircle(mCenterX, mCenterY - (mPomodoroRadius * mMinuteCircleChangeValue), mPomodoroMinuteRadius + mCenterCircleRadius * (1 - mMinuteCircleChangeValue), mMinutePaint)
            val rotateLocation = rotateLocation(-mStartAngleOffset.toFloat() - mAngle, mCenterX, mCenterY, mPomodoroRadius)
            var moveX = mCenterX - rotateLocation[0]
            var moveY = mCenterY - rotateLocation[1]

            canvas?.drawCircle(rotateLocation[0] + (moveX * mMinuteCircleChangeValue),
                    rotateLocation[1] + (moveY * mMinuteCircleChangeValue), mPomodoroMinuteRadius + ((mCenterCircleRadius - mPomodoroMinuteRadius) * mMinuteCircleChangeValue), mMinutePaint)

        } else if(mIsStartHideAnim && mCount < mMinute - 1) {
            canvas?.drawCircle(mCenterX, mCenterY, mCenterCircleRadius, mMinutePaint)

            val rotateLocation = rotateLocation(-mStartAngleOffset.toFloat(), mCenterX, mCenterY, mPomodoroRadius)
            mHideAnimPaint.alpha = ((1f - (mHideAnimValue / 1.5f)) * 255).toInt()
//            Log.d("AAAA", "${((1f - (mHideAnimValue / 1.5f)) * 255).toInt()}")
            canvas?.drawCircle(rotateLocation[0], rotateLocation[1] - (mHideAnimHeight * mHideAnimValue), mPomodoroMinuteRadius * (1.5f - mHideAnimValue), mHideAnimPaint)
        } else {
            canvas?.drawCircle(mCenterX, mCenterY, mCenterCircleRadius, mMinutePaint)
        }

        for (i in mCount..mMinute - 1) {
            var angle = mAngle * i - mStartAngleOffset
            val rotateLocation = rotateLocation(angle + mRotateRangle, mCenterX, mCenterY, mPomodoroRadius)
            if (i == 0 && mCount == 0) {
                canvas?.drawCircle(rotateLocation[0], rotateLocation[1], mPomodoroMinuteRadius * mScaleRange, mMinutePaint)
            } else if (mCount == mMinute - 1) {

            } else if (mCount != 0 && i == mMinute - 1) {
                Log.d("Pomodoro", " i == mMinute ")
                canvas?.drawCircle(rotateLocation[0], rotateLocation[1], mPomodoroMinuteRadius * mScaleRange, mMinutePaint)
            } else {
                canvas?.drawCircle(rotateLocation[0], rotateLocation[1], mPomodoroMinuteRadius, mMinutePaint)
            }
        }

    }

    private fun startMinuteCircleChangeAnim() {
        val minuteChangeAnim = ValueAnimator.ofFloat(1f)
        minuteChangeAnim.setDuration(1000)
        minuteChangeAnim.addUpdateListener {
            mMinuteCircleChangeValue = it.animatedValue as Float
            invalidate()
        }
        minuteChangeAnim.interpolator = DecelerateInterpolator()
        minuteChangeAnim.start()
    }

    private fun drawSecondCircle(canvas: Canvas?, x: Float, y: Float) {
        for (i in 0..10) {
            var angle = 360f / 10 * i - mStartAngleOffset
            val rotateLocation = rotateLocation(angle, x, y,mPomodoroMinuteRadius + mPomodoroSecondRadius + mSecondWithSecondOffset)
            canvas?.drawCircle(rotateLocation[0], rotateLocation[1], mPomodoroSecondRadius, mMinutePaint)
        }
    }

    private fun startHideCircleAnimator() {
        val hideAnimator = ValueAnimator.ofFloat(1.5f)
        hideAnimator.setDuration(1000)
        hideAnimator.interpolator = DecelerateInterpolator()
        hideAnimator.addUpdateListener {
            mHideAnimValue = it.getAnimatedValue() as Float
            invalidate()
        }
        hideAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                mIsStartHideAnim = false
            }
        })
        hideAnimator.start()
    }

    private fun startRoteAnimator() {
        if (mRotateAnimator != null) {
            mRotateAnimator?.cancel()
            mRotateAnimator = null
        }
        mRotateAnimator = ValueAnimator.ofFloat(mAngle)
        mRotateAnimator?.setDuration(1000)
        mRotateAnimator?.interpolator = DecelerateInterpolator()
        mRotateAnimator?.addUpdateListener {
            val angle = it.getAnimatedValue() as Float
            mRotateRangle = angle
            invalidate()
        }
        mRotateAnimator?.start()
    }

    private fun scaleAnimator() {
        var scaleA = ValueAnimator.ofFloat(1f, 2f)
        scaleA.setDuration(1000)
        scaleA.interpolator = DecelerateInterpolator()
        scaleA.addUpdateListener {
            mScaleRange = it.getAnimatedValue() as Float
            invalidate()
        }
        scaleA.start()
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

    private fun viewChanged() {
        mCount++
        startRoteAnimator()
        scaleAnimator()
        mIsStartHideAnim = true
        startHideCircleAnimator()
        if (mCount == mMinute -1) {
            startMinuteCircleChangeAnim()
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
           viewChanged()
            Log.d("Pomodoro", " mCount ${mCount}")
        }

        return true
    }

}