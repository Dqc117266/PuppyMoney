package com.dqc.puppymoney.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.R

class MaxLimitRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    private var mMaxWidth: Int = 0

    init {
        val ob = context.obtainStyledAttributes(attrs, R.styleable.max_limit_rv)
        mMaxWidth = ob.getDimensionPixelSize(R.styleable.max_limit_rv_maxWidth, -1)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        var needLimit = false
        if (mMaxWidth > 0) {
            needLimit = true
        }

        if (needLimit) {
            var limitWidth = measuredWidth
            if (measuredWidth > mMaxWidth) {
                limitWidth = mMaxWidth
            }
            setMeasuredDimension(limitWidth, measuredHeight)
        }
    }
}