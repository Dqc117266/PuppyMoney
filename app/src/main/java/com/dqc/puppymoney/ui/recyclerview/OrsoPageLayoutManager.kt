package com.dqc.puppymoney.ui.recyclerview

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.R

class OrsoPageLayoutManager(context: Context?,
                            orientation: Int,
                            reverseLayout: Boolean
) : LinearLayoutManager(context, orientation, reverseLayout), RecyclerView.OnChildAttachStateChangeListener{

    private var mDrife: Int = 0;
    private var onViewPagerListener: OnViewPagerListener ?= null
    private var mPagerSnapHelper: PagerSnapHelper
    private var mWishToEnterEditText: EditText ?= null;
    private var mPosition: Int = 0;
    private var mOldPosition: Int = 0;

    init {
        mPagerSnapHelper = PagerSnapHelper()
    }

    fun getViewPageListener():OnViewPagerListener {
        return onViewPagerListener!!
    }

    fun setViewPageListener(onViewPagerListener: OnViewPagerListener) {
        this.onViewPagerListener = onViewPagerListener
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        view?.addOnChildAttachStateChangeListener(this)
        mPagerSnapHelper.attachToRecyclerView(view)
        super.onAttachedToWindow(view)
    }

    override fun canScrollHorizontally(): Boolean {
        return super.canScrollHorizontally()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        mDrife = dx
        Log.d("DDD", " dx = " + dx)
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    override fun onChildViewAttachedToWindow(view: View) {
        mWishToEnterEditText = view.findViewById(R.id.wish_to_enter_edit)
        if (mDrife > 0) {
            if (onViewPagerListener != null && Math.abs(mDrife) == view.width) {
                onViewPagerListener?.onPageSelected(false, mWishToEnterEditText!!, mPosition)
            }
        } else {
            if (onViewPagerListener != null && Math.abs(mDrife) == view.height) {
                onViewPagerListener?.onPageSelected(true, mWishToEnterEditText!!, mPosition)
            }
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        mWishToEnterEditText = view.findViewById(R.id.wish_to_enter_edit)
        if (mDrife > 0) {
            if (onViewPagerListener != null) {
                onViewPagerListener?.onPageRelease(true, mWishToEnterEditText!!)
            }
        } else {
            if (onViewPagerListener != null) {
                onViewPagerListener?.onPageRelease(false, mWishToEnterEditText!!)
            }
        }
    }

    override fun onScrollStateChanged(state: Int) {
        Log.d("DDD", " stateChanged " + state)
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                var snapView = mPagerSnapHelper.findSnapView(this)
                mWishToEnterEditText = snapView?.findViewById(R.id.wish_to_enter_edit)
                mPosition = getPosition(snapView!!)
                if (onViewPagerListener != null && mOldPosition != mPosition) {
                    onViewPagerListener?.onPagePosition(mPosition)
                    onViewPagerListener?.onPageSelected(true, mWishToEnterEditText!!, mPosition)
                    mOldPosition = mPosition
                }
            }
        }
        super.onScrollStateChanged(state)
    }

}