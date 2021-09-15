package com.dqc.puppymoney.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.dqc.puppymoney.util.DisplayUtil
import com.dqc.puppymoney.util.dp2px

//            intArrayOf(0xffFF8084.toInt(), 0xffFC8D80.toInt()),
//            intArrayOf(defaultColor, towColor),

class PomodoroView3(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val ANIMATOR_DURATION: Long = 4000

    private var mIsStarting: Boolean = false
    private var mIsPause: Boolean = false

    private var mNeedStopAnimator: Boolean = false
    private var mIsStartAnimator: Boolean = false

    private var mMinute: Int = 0
    private var mCurrentMilliSecond: Long = 0
    private var mCurrentMinute = 0
    private var mCurrentSecond = 0
    private var mPauseMilliSecond: Long = 0

    private var mProgressRange: Float = 0f
    private var mProgressMax: Float = 0f
    private var mWavesRange: Float = 0f
    private var mStartPomodoroAnimatorRange: Float = 0f

    private var mCenterX: Float = 0f
    private var mCenterY: Float = 0f
    private var mRoundBgRadius: Float = 0f
    private var mRoundProgressBarRadius: Float = 0f
    private var mMainRoundRadius: Float = 0f

    private var mRoundBgPaint: Paint? = null
    private var mRoundProgressBarPaint: Paint? = null
    private var mMainRoundPaint: Paint? = null
    private var mTextPaint:Paint? = null
    private var mWavesPaint:Paint? = null
    private var mWavesAnimator: ValueAnimator? = null
    private var mStartPomodoroAnimator: ValueAnimator? = null

    private var mStringBuffer: StringBuffer = StringBuffer()


    private var mShareFactory: (centerX: Float, centerY: Float, radius:Float, color1: Int ,color2: Int) -> Shader = {centerX, centerY, radius, color1, color2 ->
        RadialGradient(
            centerX,
            centerY,
            radius,
            intArrayOf(color1, color2),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP)
    }

    fun setMinute(minute: Int) {
        if (!mIsStarting) {
            mMinute = minute
            mCurrentMinute = mMinute
            mCurrentSecond = 0
            invalidate()
        }
    }

    init {
        mRoundBgPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
            color = 0xffFFC8B8.toInt()
        }
//        mRoundBgPaint?.alpha = 255 / 2

        mRoundProgressBarPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }

        mMainRoundPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
//            color = 0xffFA7878.toInt()
            style = Paint.Style.FILL
        }
        mMainRoundPaint?.color = 0xffFA9696.toInt()

        mWavesPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = 0xffffffff.toInt()
            style = Paint.Style.FILL
        }

        mTextPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
            textSize = dp2px(20).toFloat()
            color = 0xffffffff.toInt()
        }

        wavesAnimatorInit()
        initStartPomodoroAnimaor()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = (w / 2).toFloat()
        mCenterY = (h / 2).toFloat()
        mRoundBgRadius = mCenterX * 3 / 4
        mMainRoundRadius = mCenterX / 4
        mRoundProgressBarRadius = mMainRoundRadius
        mProgressMax = mRoundBgRadius - mRoundProgressBarRadius

        mRoundBgPaint?.shader = mShareFactory(mCenterX - mRoundBgRadius / 2, mCenterY - mRoundBgRadius / 2, mRoundBgRadius * 1.5f,
            0xffffffff.toInt(),
            0xfffa9696.toInt() //fa9696
        )
        startWavesAnimator()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBacground(canvas)
        drawText(canvas)
        if (mIsStarting && !mIsPause) {
            updateTime()
            invalidate()
        }
    }

    private fun drawText(canvas: Canvas?) {
//        var bounds = Rect()
//        mTextPaint?.getTextBounds("",0, "".length, bounds)
//        var offset = (bounds.top + bounds.bottom) / 2
        mStringBuffer.append(DisplayUtil.numberAddZero(mCurrentMinute))
            .append(":")
            .append(DisplayUtil.numberAddZero(mCurrentSecond))
        val timeLocation = getCenterTextLocation(
            mStringBuffer.toString(), mTextPaint!!)

        canvas?.drawText(mStringBuffer.toString(), timeLocation[0], timeLocation[1] - ((mCenterY - timeLocation[1])), mTextPaint!!)

        val textLocation = getCenterTextLocation("工作", mTextPaint!!)
        canvas?.drawText("工作",  textLocation[0], textLocation[1] + ((mCenterY - textLocation[1]) * 1.5f) , mTextPaint!!)
        mStringBuffer.setLength(0)
    }

    private fun getCenterTextLocation(text: String, paint: Paint): FloatArray {
        val measureText = paint.measureText(text)
        val fm = paint.fontMetricsInt
        var startX = mCenterX - measureText / 2
        var startY = mCenterY - fm.descent + (fm.bottom - fm.top) / 2
        return floatArrayOf(startX, startY)
    }

    private fun updateTime() {
        var curTiming = SystemClock.elapsedRealtime() - mCurrentMilliSecond
        var countDown = mMinute * 60 * 1000 - curTiming

        mProgressRange = curTiming / (mMinute * 60 * 1000f)

        if (countDown > 0) {
            mCurrentMinute = (countDown / 1000 / 60).toInt()
            mCurrentSecond = (countDown / 1000 % 60).toInt()
        } else {
            cancelPomodoro()
        }
    }

    private fun drawBacground(canvas: Canvas?) {
        canvas?.drawCircle(mCenterX, mCenterY, mMainRoundRadius + ((mRoundBgRadius - mMainRoundRadius) * mStartPomodoroAnimatorRange), mRoundBgPaint!!)
        canvas?.drawCircle(mCenterX, mCenterY, mRoundProgressBarRadius + (mProgressMax * mProgressRange), mRoundProgressBarPaint!!)
        canvas?.drawCircle(mCenterX, mCenterY, mRoundProgressBarRadius + ((mProgressMax * mProgressRange) * mWavesRange), mWavesPaint!!)
        canvas?.drawCircle(mCenterX, mCenterY, mRoundProgressBarRadius, mMainRoundPaint!!)
        mRoundProgressBarPaint?.shader = mShareFactory(mCenterX, mCenterY, mRoundProgressBarRadius + (mProgressMax * mProgressRange),
            0xffFA9696.toInt(),
            0xffFFD2D2.toInt()
        )
    }

    private fun wavesAnimatorInit() {
        mWavesAnimator = ValueAnimator.ofFloat(1f)
        mWavesAnimator?.duration = ANIMATOR_DURATION
        mWavesAnimator?.interpolator = DecelerateInterpolator()
        mWavesAnimator?.repeatCount = ValueAnimator.INFINITE
        mWavesAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator?) {
                if (mNeedStopAnimator) {
                    mNeedStopAnimator = false
                    mWavesAnimator?.cancel()
                }
            }

        })
        mWavesAnimator?.addUpdateListener {
            mWavesRange = it.getAnimatedValue() as Float
            mWavesPaint!!.alpha = (255 * 3 / 4 * (1 - mWavesRange)).toInt()
            if (mNeedStopAnimator) {
                invalidate()
            }
        }
    }

    private fun initStartPomodoroAnimaor() {
        mStartPomodoroAnimator = ValueAnimator.ofFloat(1f)
        mStartPomodoroAnimator?.duration = 400
        mStartPomodoroAnimator?.interpolator = DecelerateInterpolator()
        mStartPomodoroAnimator?.addUpdateListener {
            if (mIsStartAnimator) {
                mStartPomodoroAnimatorRange = it.getAnimatedValue() as Float
            } else {
                mStartPomodoroAnimatorRange = 1 - it.getAnimatedValue() as Float
            }
            if (!mIsStarting && !mIsPause) {
                invalidate()
            }
        }
    }

    private fun startPomodoroAnimaor() {
        initStartPomodoroAnimaor()
        mIsStartAnimator = true
        if (mStartPomodoroAnimator != null && !mStartPomodoroAnimator?.isStarted!!) {
            mStartPomodoroAnimator?.start()
        }
    }

    private fun cancelPomodoroAnimaor() {
        initStartPomodoroAnimaor()
        mIsStartAnimator = false
        if (mStartPomodoroAnimator != null && !mStartPomodoroAnimator?.isStarted!!) {
            mStartPomodoroAnimator?.start()
        }
    }

    private fun startWavesAnimator() {
        mNeedStopAnimator = false
        if (mWavesAnimator != null && !mWavesAnimator?.isStarted!!) {
            mWavesAnimator?.start()
        } else if (mWavesAnimator != null && mWavesAnimator?.isStarted!!) {
            mWavesAnimator?.resume()
        }
    }

    fun startPomodoro() {
        if (!mIsStarting) {
            mIsStarting = true
            mCurrentMilliSecond = SystemClock.elapsedRealtime()
            startPomodoroAnimaor()
        }
        if (mIsPause) {
            mIsPause = false
            mCurrentMilliSecond += (SystemClock.elapsedRealtime() - mPauseMilliSecond)
        }
        startWavesAnimator()
        invalidate()
    }

    fun pausePomodoro() {
        if (!mIsPause) {
            mIsPause = true
            mNeedStopAnimator = true
            mPauseMilliSecond = SystemClock.elapsedRealtime()
            invalidate()
        }
    }

    fun cancelPomodoro() {
        if (mIsStarting) {
            mIsStarting = false
            mIsPause = false
            mWavesRange = 0f
            mProgressRange = 0f
            mCurrentMinute = mMinute
            mCurrentSecond = 0
            mNeedStopAnimator = true
            cancelPomodoroAnimaor()
            invalidate()
        }
    }
}