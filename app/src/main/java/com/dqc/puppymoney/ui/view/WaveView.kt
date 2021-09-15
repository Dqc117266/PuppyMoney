package com.dqc.puppymoney.ui.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import com.dqc.puppymoney.R


class WaveView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mWidth = 0
    private var mHeight = 0
    private var mShapePath: Path? = null;
    private var step = 20

    private var omega: Double = 0.0
    private var phi: Double = 0.0
    private var delta = -2
    private var fillTop = true
    private var startColor = 0
    private var endColor = 0
    private var isStop: Boolean = false
    private var stopAnimValue = 1f
//    private var waveColor: Int = 0
    private var fillPaint = Paint().apply {
        strokeWidth = 1f
        style = Paint.Style.FILL
    }

    init {
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.WaveView)
//        waveColor = typedArray.getColor(R.styleable.WaveView_color, Color.parseColor("#3958AA"));
        fillTop = typedArray.getInt(R.styleable.WaveView_fill_mode, 1) == 1;// 默认绘制上层
        delta = typedArray.getInt(R.styleable.WaveView_speed, -2); //默认向右移动
        omega = typedArray.getFloat(R.styleable.WaveView_omega, 3 * 1.0f / 4).toDouble();// 角频率
        phi = typedArray.getInt(R.styleable.WaveView_phi, 0) * Math.PI / 180 + Math.PI / 2 * -1;// 初始相位角
        startColor = typedArray.getColor(R.styleable.WaveView_startColor, 0xff000000.toInt())
        endColor = typedArray.getColor(R.styleable.WaveView_endColor, 0xff000000.toInt())
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mShapePath = Path()

        if (fillTop)
            mShapePath!!.moveTo(0f, 0f)
        else
            mShapePath!!.moveTo(0f, mHeight + 1f)

        var x = 0
        while (x <= mWidth) {
            val angle = x * 1.0f / mWidth * 2 * Math.PI // 弧度
            val y = (mHeight / 10 * Math.sin(angle * omega + phi)) * stopAnimValue
//            Log.d("WavaView", " height:${mHeight} angle:${angle} omega:${omega} phi:${phi} sin:${Math.sin(angle * omega + phi)} y:${y}")
            mShapePath!!.lineTo(x.toFloat(), y.toFloat() + mHeight / 2)
            x += step
        }

        if (fillTop) {
            mShapePath!!.lineTo(mWidth.toFloat(), 0F)
        } else {
            mShapePath!!.lineTo(mWidth.toFloat(), (mHeight + 1).toFloat())
        }
        canvas!!.drawPath(mShapePath!!, fillPaint)

//        postInvalidateDelayed(25) // 每秒刷新40次
        invalidate()
        addPhi()
    }

    /**
     * 增加相位角
     */
    private fun addPhi() {
        phi += delta * Math.PI / 180
        if (phi > Math.PI * 2) {
            phi -= Math.PI * 2;
        } else if (phi < Math.PI * - 2) {
            phi += Math.PI * 2
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        /**
         * 组件测量
         * 布局里指定的size使用布局的size
         * 属性里指定的size使用属性的size
         * 都没有指定，使用默认size
         */
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

        var mLinearGradient = LinearGradient(mWidth.toFloat() / 2, 0f, mWidth.toFloat() / 2, mHeight.toFloat(),
                intArrayOf(startColor, endColor), null, Shader.TileMode.MIRROR)
        fillPaint.shader = mLinearGradient
        setMeasuredDimension(widthSize, heightSize)
    }

    private fun waveAnimator(isStop: Boolean) {
        var startValue = 1f
        var endValue = 0.15f

        if (!isStop) {
            startValue = 0.15f
            endValue = 1f
        }

        val ofFloat = ValueAnimator.ofFloat(startValue, endValue)
        ofFloat.setDuration(1000)
        ofFloat.interpolator = AccelerateInterpolator()
        ofFloat.addUpdateListener {
            stopAnimValue = it.getAnimatedValue() as Float
        }
        ofFloat.start()
    }

    fun stopWava() {
        waveAnimator(true)
    }

    fun startWava() {
        waveAnimator(false)
    }

}