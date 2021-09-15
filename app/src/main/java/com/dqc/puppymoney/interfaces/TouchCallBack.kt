package com.dqc.puppymoney.interfaces

interface TouchCallBack {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDelete(position: Int)
    fun onClearView()
}