package com.dqc.puppymoney.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishToChooseBean

class WishToChooseAdapter : RecyclerView.Adapter<WishToChooseAdapter.ViewHolder>() {

    private var mWishList: ArrayList<WishToChooseBean> = ArrayList()
    private var mLayoutManager: LinearLayoutManager ?= null
    private var mLastSelectedPosition: Int = 0
    private var mOnWishToChooseCompleteLienter: OnWishToChooseCompleteLienter ?= null
    private var mIsNeedHideBtn: Boolean = false

    fun setWishList(wishList: Array<String>) {
        for (i in 0..wishList.size - 1) {
            mWishList.add(WishToChooseBean(0, i, wishList.get(i), false))
        }
    }

    fun setOnWishToChooseCompleteLienter(onWishToChooseCompleteLienter: OnWishToChooseCompleteLienter) {
        mOnWishToChooseCompleteLienter = onWishToChooseCompleteLienter
    }

    fun setLayoutManager(layoutManager: LinearLayoutManager) {
        mLayoutManager = layoutManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.wish_to_choose_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chooseItemtv.text = mWishList.get(position).wishText
        holder.itemView.setOnClickListener {
            if (mWishList.get(position).isSelected) {
                mWishList.get(position).isSelected = false
                holder.selectImage.visibility = View.GONE
            } else {
                mWishList.get(position).isSelected = true
                holder.selectImage.visibility = View.VISIBLE
            }

            var selectedCount = getSelectedCount()
            if (selectedCount == 3) {
                mLastSelectedPosition = position
                if (mOnWishToChooseCompleteLienter != null) {
                    mOnWishToChooseCompleteLienter?.onWishToChooseComplete(true)
                }
                mIsNeedHideBtn = true
            } else if (selectedCount > 3) {
                mWishList.get(mLastSelectedPosition).isSelected = false
                mLayoutManager?.findViewByPosition(mLastSelectedPosition)!!
                        .findViewById<ImageView>(R.id.wish_to_choose_select_iv).visibility = View.GONE
                mLastSelectedPosition = position
            } else {
                if (mIsNeedHideBtn) {
                    if (mOnWishToChooseCompleteLienter != null) {
                        mOnWishToChooseCompleteLienter?.onWishToChooseComplete(false)
                    }
                    mIsNeedHideBtn = false
                }
            }
        }
    }

    fun getSelectedCount() : Int {
        var count = 0
        for (i in 0..mWishList.size - 1) {
            if (mWishList.get(i).isSelected) {
                count ++
            }
        }
        return count
    }

    override fun getItemCount(): Int {
        return mWishList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getWishList(): ArrayList<WishToChooseBean> {
        return mWishList
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var chooseItemtv: TextView
        var selectImage: ImageView
        init {
            chooseItemtv = view.findViewById(R.id.wish_to_choose_item_tv)
            selectImage = view.findViewById(R.id.wish_to_choose_select_iv)
        }
    }

    interface OnWishToChooseCompleteLienter {
        fun onWishToChooseComplete(isChooseComplete: Boolean)
    }

}