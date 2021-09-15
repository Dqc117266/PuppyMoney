package com.dqc.puppymoney.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.dqc.puppymoney.util.dp2px

class PomodoroView2(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var LITTLE_ROUND_COUNT: Int = 25
    private var mPaint: Paint? = null
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mMinute:Int = 0
    private var mSecond:Int = 0
    private var mLinearGradient: LinearGradient? = null
    private var mRemainColor: Int = Color.parseColor("#ffffffff")
    private var mAfterUseColor: Int = Color.parseColor("#ff494949")
    private var mLittleRoundWidth: Int = dp2px(16)
    private var mViewWidthRatio:Float = 0.4f
    private var mLittleRectF: RectF? = null

    init {
        mPaint = Paint()
        mPaint?.style = Paint.Style.FILL
        mPaint?.isDither = true
        mPaint?.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthsize = MeasureSpec.getSize(widthMeasureSpec)
        val heightsize = MeasureSpec.getSize(heightMeasureSpec)
        mCenterX = widthsize.toFloat() / 2
        mCenterY = heightsize.toFloat() / 2
    }

    fun setTime(minute: Int, second: Int) {
        mMinute = minute
        mSecond = second
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawProfressBar(canvas)
    }

    fun drawProfressBar(canvas: Canvas?) {
        mLittleRectF = RectF(
            mCenterX,
            mCenterY - measuredWidth * mViewWidthRatio,
            mCenterX + mLittleRoundWidth,
            mCenterY - (measuredWidth * mViewWidthRatio + mLittleRoundWidth)
        )

        for (i in 0..LITTLE_ROUND_COUNT - 1) {
            if (findRefreshRoundByTime(mMinute) == i) {
                drawLittleRect(canvas, true)
            } else if(findRefreshRoundByTime(mMinute) > i) {
                mPaint?.color = mAfterUseColor
                drawLittleRect(canvas, false)
            } else {
                mPaint?.color = mRemainColor
                drawLittleRect(canvas, false)
            }
            canvas?.save()
        }
        canvas?.restore()
    }

    fun drawLittleRect(canvas: Canvas?, isLittleRefresh: Boolean) {
        if (isLittleRefresh) {
            mLinearGradient = LinearGradient(
                mCenterX,                             // 初始化渐变器 ,
                mCenterY - measuredWidth * mViewWidthRatio,
                mCenterX + mLittleRoundWidth,
                mCenterY - measuredWidth * mViewWidthRatio,
                intArrayOf(mAfterUseColor, mRemainColor),
                floatArrayOf((1 - mSecond / 60f), (1 - mSecond / 60f)),
                Shader.TileMode.CLAMP
            )
            mPaint?.setShader(mLinearGradient)
        } else {
            if (mPaint?.shader != null) {
                mPaint?.shader = null
            }
        }
        mPaint?.alpha = 255 / 3
        canvas?.drawRoundRect(mLittleRectF!!,
            this.dp2px(10).toFloat(), this.dp2px(10).toFloat(), mPaint!!)
        canvas?.rotate((360f / LITTLE_ROUND_COUNT), mCenterX, mCenterY)
    }

    fun findRefreshRoundByTime(minute: Int) :Int{ //通过分钟来获取某个要刷新进度条的圆
        return (LITTLE_ROUND_COUNT - 1) - minute
    }

}