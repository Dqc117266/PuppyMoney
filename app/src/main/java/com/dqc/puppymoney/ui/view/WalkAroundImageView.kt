package com.dqc.puppymoney.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.dqc.puppymoney.R
import com.dqc.puppymoney.util.DisplayUtil

@SuppressLint("AppCompatCustomView")
class WalkAroundImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    private var mViewMaxWidth = 0
    private var mViewMaxHeight = 0
    private var mRadius: Float = 0f
    private var mMatrix: Matrix? = null
    private var mBitmap: Bitmap? = null
    private var mValueAnimator: ValueAnimator? = null
    private var mAnimatorValue: Float = 0f
    private lateinit var mBitmapPaint: Paint
    private var mIsCanStartAnimator: Boolean = false

    init {
        var a = context?.obtainStyledAttributes(attrs, R.styleable.walk_around_image)
        mRadius = a!!.getDimensionPixelSize(R.styleable.walk_around_image_radius, 0).toFloat()
    }

    fun initBitmap() {
        var bmp = DisplayUtil.getBitmapFromDrawable(drawable)
        scaleBitmap(bmp!!)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (width != 0 && height != 0) {
            initBitmap()

            if (mValueAnimator != null && !mValueAnimator?.isStarted!!) {
                startAnimator()
            }
        }
    }

    fun scaleBitmap(bmp: Bitmap) {
        if (bmp.width > bmp.height) {
            var ratio:Float = bmp.width / bmp.height.toFloat()
            Log.d("ScaleBitmap", " rotio $ratio w = ${bmp.width} h = ${bmp.height}")

            mIsCanStartAnimator = isCanStartAnimator(bmp.width, bmp.height)
            mBitmap = Bitmap.createScaledBitmap(bmp, (mViewMaxHeight * ratio).toInt(), mViewMaxHeight, true)
        } else if (bmp.width < bmp.height) {
            var ratio:Float = bmp.height / bmp.width.toFloat()

            mIsCanStartAnimator = isCanStartAnimator(bmp.height, bmp.width)
            mBitmap = Bitmap.createScaledBitmap(bmp, mViewMaxWidth, (mViewMaxWidth * ratio).toInt(), true)
        } else {
            mBitmap = Bitmap.createScaledBitmap(bmp, mViewMaxWidth, mViewMaxWidth, true)
        }

        mMatrix = Matrix()

        mBitmapPaint = Paint().apply {
            isDither = true
            isAntiAlias = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }
//        startAnimator()
    }

    fun isCanStartAnimator(value: Int, value1: Int): Boolean {
        return if (Math.abs(value - value1) >= DisplayUtil.dip2px(context, 20)) true else false
    }

    fun setImageRadius(radius: Float) {
        mRadius = radius
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY
            || heightMode == MeasureSpec.EXACTLY) {
            mViewMaxWidth = widthSize
            mViewMaxHeight = widthSize
        } else {
            mViewMaxWidth = DisplayUtil.dip2px(context!!, 350).toInt()
            mViewMaxHeight = DisplayUtil.dip2px(context!!, 350).toInt()
        }
        setMeasuredDimension(mViewMaxWidth, mViewMaxHeight)
        Log.d("WalkImageView", " onMeasure")
        initBitmap()
    }

    override fun onDraw(canvas: Canvas?) {
        drawRoundRadius(canvas)
//        super.onDraw(canvas)
        if (mBitmap?.width!! > mBitmap?.height!!) {
            mMatrix?.setTranslate(-(mBitmap?.width!! - mViewMaxWidth) * mAnimatorValue, 0f)
        } else if (mBitmap?.width!! < mBitmap?.height!!) {
            mMatrix?.setTranslate(0f, -(mBitmap?.height!! - mViewMaxHeight) * mAnimatorValue)
        }
        canvas?.drawBitmap(mBitmap!!, mMatrix!!, mBitmapPaint)
    }

    fun drawRoundRadius(canvas: Canvas?) {
        if (width >= mRadius && height > mRadius) {
            var path = Path()
            //四个圆角
            path.moveTo(mRadius, 0f)
            path.lineTo(width - mRadius, 0f)
            path.quadTo(width.toFloat(), 0f, width.toFloat(), mRadius)
            path.lineTo(width.toFloat(), height - mRadius)
            path.quadTo(width.toFloat(), height.toFloat(), width - mRadius, height.toFloat())
            path.lineTo(mRadius, height.toFloat())
            path.quadTo(0f, height.toFloat(), 0f, height - mRadius)
            path.lineTo(0f, mRadius)
            path.quadTo(0f, 0f, mRadius, 0f)

            canvas?.clipPath(path)
        }
    }

    fun stopAnimator() {
        if (mValueAnimator != null) {
            mValueAnimator?.cancel()
            mValueAnimator = null
            mAnimatorValue = 0f
        }
    }

    fun startAnimator() {
        if (mIsCanStartAnimator) {
            var isAnimationEnd = true
            mValueAnimator = ValueAnimator.ofFloat(0f, 1f)
            mValueAnimator?.interpolator = DecelerateInterpolator()
            mValueAnimator?.duration = 1000 * 20
            mValueAnimator?.repeatCount = ValueAnimator.INFINITE
            mValueAnimator?.addUpdateListener {
                val value = it.getAnimatedValue() as Float
                if (value != 1f) {
                    if (isAnimationEnd) {
                        mAnimatorValue = it.getAnimatedValue() as Float
                    } else {
                        mAnimatorValue = 1f - value
                    }
                    invalidate()
                }
            }
            mValueAnimator?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator?) {
                    super.onAnimationRepeat(animation)
                    isAnimationEnd = !isAnimationEnd
                    Log.d("CCC", " is A " + isAnimationEnd)
                }
            })
            mValueAnimator?.start()
        }
    }
}