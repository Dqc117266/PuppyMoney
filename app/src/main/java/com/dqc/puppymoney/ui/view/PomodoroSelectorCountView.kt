package com.dqc.puppymoney.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.SelectPomodorBean
import com.dqc.puppymoney.interfaces.IPomCountCallBack
import com.dqc.puppymoney.util.dp2px

class PomodoroSelectorCountView(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private val MAX_COUNT = 4
    private var mSelectPomoList = ArrayList<SelectPomodorBean>()
    private var mWidthAndHeigt = dp2px(30)
    private var mPaddingSize = dp2px(16)
    private var mSelectCount = 0
    private var mIsDownPress = false
    private var mLastSelectCount = -1
    private var mIPomCountCallBack: IPomCountCallBack? = null

    private var mViewLocalX: IntArray = IntArray(MAX_COUNT)

    init {
        for (i in 0..MAX_COUNT - 1) {
            var imageView = ImageView(context)
            imageView.setImageResource(R.drawable.pomodoro_ic)
            if (i == 0) {
                imageView.imageTintList = ColorStateList.valueOf(context!!.resources.getColor(R.color.success_diaru_pomodoro_select_ic_color))
            }
            mSelectPomoList.add(SelectPomodorBean(imageView, false))
            addView(imageView)
        }
    }

    fun setIPomCountCallBack(pomCountCallBack: IPomCountCallBack?) {
        mIPomCountCallBack = pomCountCallBack
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = mWidthAndHeigt * MAX_COUNT + mPaddingSize * (MAX_COUNT - 1)
        setMeasuredDimension(width, mWidthAndHeigt)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var right = mWidthAndHeigt
        for (i in 0..childCount - 1) {
            val childAt = getChildAt(i) as ImageView
            childAt.layout((right - mWidthAndHeigt),0,(right), mWidthAndHeigt)
            mViewLocalX[i] = right - mWidthAndHeigt
            right += (mWidthAndHeigt + mPaddingSize)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsDownPress = true
            }

            MotionEvent.ACTION_MOVE -> {

                Log.d("getCountSelect", " " + getTouchLocationCount(event?.x.toInt()))

                if (getTouchLocationCount(event?.x.toInt()) != -1) {
                    val touchLocationCount = getTouchLocationCount(event?.x.toInt())
                    setSelectCountView(touchLocationCount)
                }

            }

            MotionEvent.ACTION_UP -> {
                if (mIsDownPress) {
                    if (getTouchLocationCount(event?.x.toInt()) != -1) {
                        val touchLocationCount = getTouchLocationCount(event?.x.toInt())
                        setSelectCountView(touchLocationCount)
                    }
                }
            }

        }
        return true
    }

    private fun setSelectCountView(selectCount: Int) {
        if (selectCount == -1) {
            return
        }
        if (mLastSelectCount != -1 && mLastSelectCount != selectCount) {
            if (mLastSelectCount > selectCount) {
                for (i in selectCount + 1..mLastSelectCount) {
                    val imageView = mSelectPomoList.get(i).mView as ImageView
                    imageView.imageTintList = ColorStateList.valueOf(context!!.resources.getColor(R.color.success_diaru_pomodoro_unselect_ic_color))
                }
            } else {
                for (i in mLastSelectCount..selectCount) {
                    val imageView = mSelectPomoList.get(i).mView as ImageView
                    imageView.imageTintList = ColorStateList.valueOf(context!!.resources.getColor(R.color.success_diaru_pomodoro_select_ic_color))
                }
            }
            mSelectCount = selectCount
            if (mIPomCountCallBack != null) {
                mIPomCountCallBack?.onPomCount(mSelectCount)
            }

        } else if (mLastSelectCount == -1) {
            mSelectCount = selectCount
            for (i in 0..selectCount) {
                val imageView = mSelectPomoList.get(i).mView as ImageView
                imageView.imageTintList = ColorStateList.valueOf(context!!.resources.getColor(R.color.success_diaru_pomodoro_select_ic_color))
            }
            if (mIPomCountCallBack != null) {
                mIPomCountCallBack?.onPomCount(mSelectCount)
            }
        }
        mLastSelectCount = selectCount
    }

    private fun getTouchLocationCount(x: Int):Int {
        var count = -1
        for (i in 0..mViewLocalX.size - 1) {
            if (mViewLocalX[i] <= x) {
                count = i
            }
        }
        return count
    }

    fun getSelelctCount(): Int {
        return mSelectCount
    }

}