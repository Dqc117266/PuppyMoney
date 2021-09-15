package com.dqc.puppymoney.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.dqc.puppymoney.R

@SuppressLint("NewApi")
class SuccessDiaryProgressBar(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mItemWidth = 0f
    private var mItemHeigh = 0f
    private var mItemCount = 0
    private var mCompletedColor = 0
    private var mUnCompletedColor = 0
    private var mItemSpace = 0f
    private var mProgressPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private var mCompletedCount = 6

    init {
        if (attrs != null) {
            var a = context?.obtainStyledAttributes(attrs, R.styleable.success_diary_progress)
            mItemWidth = a!!.getDimensionPixelSize(R.styleable.success_diary_progress_item_width, 0).toFloat()
            mItemHeigh = a.getDimensionPixelSize(R.styleable.success_diary_progress_item_height, 0).toFloat()
            mItemSpace = a.getDimensionPixelSize(R.styleable.success_diary_progress_item_space, 0).toFloat()

            mItemCount = a.getInteger(R.styleable.success_diary_progress_item_count, 5)

            var completedColor = context?.getColor(R.color.success_diary_default_completed_color)
            var unCompletedColor = context?.getColor(R.color.success_diary_default_uncompleted_color)

            mCompletedColor = a.getColor(R.styleable.success_diary_progress_completed_color, completedColor!!)
            mUnCompletedColor = a.getColor(R.styleable.success_diary_progress_uncompleted_color, unCompletedColor!!)
            mProgressPaint.color = mUnCompletedColor
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var viewWidth: Int = ((mItemWidth + mItemSpace) * mItemCount).toInt()
        setMeasuredDimension(viewWidth, mItemHeigh.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawProgressBar(canvas)
    }

    fun setCompletedCount(completedCount: Int) {
        mCompletedCount = completedCount
        invalidate()
    }

    private fun drawProgressBar(canvas: Canvas?) {
        var left = 0
        for (i in 0..mItemCount - 1) {
            if (i < mCompletedCount) {
                mProgressPaint.color = mCompletedColor
            } else {
                mProgressPaint.color = mUnCompletedColor
            }

            var rect = Rect(left, 0, (left + mItemWidth).toInt(), mItemHeigh.toInt())
            canvas?.drawRect(rect, mProgressPaint)
            left += (mItemWidth + mItemSpace).toInt()
        }
    }
}