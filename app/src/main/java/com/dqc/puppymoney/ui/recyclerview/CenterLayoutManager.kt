package com.dqc.puppymoney.ui.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CenterLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        val centerSmoothScroller = CenterSmoothScroller(recyclerView?.context)
        centerSmoothScroller.targetPosition = position
        startSmoothScroll(centerSmoothScroller)
    }

    private class CenterSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
        private val MILLISECONDS_PER_INCH = 100f

        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            return MILLISECONDS_PER_INCH / displayMetrics!!.densityDpi
        }
    }

}