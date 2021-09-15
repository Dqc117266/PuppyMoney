package com.dqc.puppymoney.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup

class AViewGroup(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childAt = getChildAt(0)
        childAt.layout(0, 0, 400, 400)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("AViewGroup", "onInterceptTouchEvent ${super.onInterceptTouchEvent(ev)}")
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("AViewGroup", "dispatchTouchEvent ${super.dispatchTouchEvent(ev)}")
        return super.dispatchTouchEvent(ev)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {Log.d("AViewGroup", " ACTION_DOWN ")}
            MotionEvent.ACTION_UP -> {Log.d("AViewGroup", " ACTION_UP ")}
            MotionEvent.ACTION_MOVE -> {Log.d("AViewGroup", " ACTION_MOVE ")}
            MotionEvent.ACTION_CANCEL -> {Log.d("AViewGroup", " ACTION_CANCEL ")}
        }
        return true
    }
}