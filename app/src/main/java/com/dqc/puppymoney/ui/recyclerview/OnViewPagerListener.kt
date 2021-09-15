package com.dqc.puppymoney.ui.recyclerview

import android.view.View

interface OnViewPagerListener {
    fun onPageSelected(select: Boolean, view: View?, position: Int)
    fun onPageRelease(isNest: Boolean, view: View)
    fun onPagePosition(position: Int)
}