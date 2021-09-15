package com.dqc.puppymoney.ui.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.util.DisplayUtil

class  DotViews(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private var dotViewWidth = DisplayUtil.dip2px(context!!, 20)
    private var leftOffset = DisplayUtil.dip2px(context!!, 12)
    private var mSelectPosition: Int = 0
    private var mPreviousPosition: Int = 0
    private var mRecyclerView: RecyclerView ?= null
    private var mCurEditText: EditText ?= null
    private var mPreviousEditText: EditText ?= null
    private var mInputCompleteText = arrayOfNulls<String>(10)
    private var mIsAllInputComplete: Boolean = false
    private var mOnIsAllInputCompleteListener: OnAllInputCompleteListener ?= null

    init {
        for (i in 0..9) {
            addView(DotView(context))
        }
        for (i in 0..9) {
            mInputCompleteText.set(i, "")
        }
        updateDotView()
    }

    fun setSelectPosition(editText: EditText, selectPosition: Int) {
        mSelectPosition = selectPosition
        mCurEditText = editText
        updateDotView()
    }

    fun setOnAllInputCompleteListener(onAllInputCompleteListener: OnAllInputCompleteListener) {
        mOnIsAllInputCompleteListener = onAllInputCompleteListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d("DQCCCC", "onMeasure")

        var width = dotViewWidth * 10 + leftOffset * 9
        setMeasuredDimension(width.toInt(), dotViewWidth.toInt())
    }

    private fun updateDotView() {
        if (mSelectPosition >= 0 && mSelectPosition <= 9) {
            if (mPreviousPosition != mSelectPosition) {
                val dotView = getChildAt(mPreviousPosition) as DotView
                dotView.setIsSelected(false)
                if (mPreviousEditText != null) {
                    var isExistsText = mPreviousEditText?.text.toString().length > 0
                    dotView.setIsExistText(isExistsText)

                    if (isExistsText) {
                        mInputCompleteText.set(mPreviousPosition, mPreviousEditText?.text.toString())
                    } else {
                        mInputCompleteText.set(mPreviousPosition, "")
                    }
                }
            }

            val dotView = getChildAt(mSelectPosition) as DotView
            dotView.setIsSelected(true)

            if (getInputCompleteCount() >= mInputCompleteText.size - 1) {
                mCurEditText?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        mIsAllInputComplete = (mCurEditText?.text.toString().length > 0)
                        if (mIsAllInputComplete) {
                            mInputCompleteText.set(mSelectPosition, mPreviousEditText?.text.toString())
                        } else {
                            mInputCompleteText.set(mSelectPosition, "")
                        }
                        if (mOnIsAllInputCompleteListener != null) {
                            mOnIsAllInputCompleteListener?.onIsAllInputComplete(getInputCompleteCount() == 10)
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }
                })
            }
            Log.d("QQQQQ", " count = " + getInputCompleteCount())
            mPreviousPosition = mSelectPosition
            mPreviousEditText = mCurEditText
        }
    }

    fun getIsAllInputComplete() : Boolean {
        return mIsAllInputComplete
    }

    fun getAllInputText(): Array<String?> {
        return mInputCompleteText
    }

    fun getInputCompleteCount() : Int {
        var count = 0
        for (i in 0..mInputCompleteText.size - 1) {
            if (mInputCompleteText.get(i)!!.length > 0) {
                count ++
            }
        }
        return count
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d("DQCCCC", "onLayout")
        var right = dotViewWidth
        for (i in 0..childCount - 1) {
            val childAt = getChildAt(i) as DotView
            childAt.layout((right - dotViewWidth).toInt(),0,(right).toInt(), dotViewWidth.toInt())
            right += (dotViewWidth + leftOffset).toInt()
        }
    }

    fun setRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        for (i in 0..9) {
            getChildAt(i).setOnTouchListener(object : OnTouchListener {
                var isClick = false
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isClick = true
                        }

                        MotionEvent.ACTION_UP -> {
                            if (isClick && isViewTouchRange(getChildAt(i), event.rawX.toInt(), event.rawY.toInt())) {
                                mSelectPosition = i
                                mRecyclerView?.smoothScrollToPosition(mSelectPosition)
                            }
                        }

                        MotionEvent.ACTION_CANCEL -> {
                            isClick = false
                        }
                    }

                    return true
                }
            })
        }
        return super.onTouchEvent(event)
    }

    fun isViewTouchRange(view: View, x: Int, y:Int):Boolean {
        val intArray = IntArray(2)
        view.getLocationInWindow(intArray)
        var left = intArray[0]
        var top = intArray[1]
        var right = left + dotViewWidth
        var bottom = top + dotViewWidth
        return (x >= left && x <= right && y >= top && y <= bottom)
    }

    interface OnAllInputCompleteListener {
        fun onIsAllInputComplete(isAllInputComplete: Boolean)
    }
}