package com.dqc.puppymoney.ui.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishPhotoAlbumBean
import com.dqc.puppymoney.ui.view.CustomFontTextView
import com.dqc.puppymoney.ui.view.WalkAroundImageView
import com.dqc.puppymoney.util.DisplayUtil
import java.io.File

class WishPhotoPageAdapter: RecyclerView.Adapter<WishPhotoPageAdapter.PageViewHolder>() {

    private var mWishPhotoList = ArrayList<WishPhotoAlbumBean>()
    private var mPhotoPageTouchLienter: IPhotoPageTouchLienter? = null
    private var mContext: Context? = null
    private var mIsEditMode: Boolean = false
    private var mSelectedList: BooleanArray? = null
    public var mCurPostion: Int = 0

    fun setPhotoPageTouchLienter(photoPageTouchLienter: IPhotoPageTouchLienter) {
        mPhotoPageTouchLienter = photoPageTouchLienter
    }

    fun setWishPhotoList(wishPhotoList: ArrayList<WishPhotoAlbumBean>) {
        mWishPhotoList = wishPhotoList
        mSelectedList = BooleanArray(mWishPhotoList.size)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        mContext = parent.context
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.wish_photo_page_item, parent, false)
        return PageViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
//        Glide.with(mContext)
//                .load(File(mWishPhotoList.get(position).imgPath))
//                .asBitmap()
//                .dontAnimate()
//                .into(object : SimpleTarget<Bitmap>() {
//                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
//                        holder.mWalkAroundIv.setImageBitmap(resource)
//                    }
//                })
        mCurPostion = position
        Log.d("WishPhotoPageAdapter", " onBindViewHolder " +  mWishPhotoList.get(position) + " mIsEditMode " + mIsEditMode)

//        for (i in 0..mWishPhotoList.size - 1) {
//            Log.d("WishPhotoPageAdapter", " onBindViewHolder " + mWishPhotoList.get(i).imgPath + " mIsEditMode " + mIsEditMode + " pos " + i)
//
//        }

        val wishPhotoBean = mWishPhotoList.get(position)

        val orientationRotate = DisplayUtil.getOrientationRotate(wishPhotoBean.imgPath)
        var bitmap = DisplayUtil.rotateBitmapByDegree(BitmapFactory
            .decodeFile(wishPhotoBean.imgPath), orientationRotate)

        holder.mWalkAroundIv.setImageBitmap(bitmap)

        val currentTime = DisplayUtil.getCurrentTime()
        var yearText = if (wishPhotoBean.mAddYear.equals(Integer.toString(currentTime.mCurrentYear) + "年")) "今年" else wishPhotoBean.mAddYear
        holder.mShowYearsTv.setText(yearText)
        var monthText = "${wishPhotoBean.mAddMonthDay}  ${wishPhotoBean.mAddHourMinute}"
        holder.mShowTimeTv.setText(monthText)

        holder.mWalkAroundIv.setOnClickListener {

            if (mPhotoPageTouchLienter != null
                    && !mIsEditMode) {
                mPhotoPageTouchLienter?.onClickPhotoPage(position, mWishPhotoList.get(position).imgPath, holder)
            }

            if (mIsEditMode) {
                editModeSelectedPosition(holder.mSelectWalkAroundBg, position)
            }

        }

        editModeSelectItem(holder.mSelectWalkAroundBg, mSelectedList?.get(position)!!)

        holder.mWalkAroundIv.setOnLongClickListener {
            mIsEditMode = !mIsEditMode

            if (mPhotoPageTouchLienter != null) {
                mPhotoPageTouchLienter?.onEditModeChanged(mIsEditMode)
            }

            if (!mIsEditMode) {
                resetSelectList()

                if (mPhotoPageTouchLienter != null) {
                    mPhotoPageTouchLienter?.onEditModeSelectItem(mSelectedList!!, position)
                }
            } else {
                editModeSelectedPosition(holder.mSelectWalkAroundBg, position)
            }

            true
        }

    }

    fun resetSelectList() {
        for (i in 0..mSelectedList!!.size - 1) {
            mSelectedList?.set(i, false)
        }
        notifyDataSetChanged()
    }

    fun editModeSelectedPosition(view: View, position: Int) {
        var curSelected = !mSelectedList!!.get(position)
        mSelectedList?.set(position, curSelected)
        editModeSelectItem(view, curSelected)

        if (mPhotoPageTouchLienter != null) {
            mPhotoPageTouchLienter?.onEditModeSelectItem(mSelectedList!!, position)
        }
    }

    fun editModeSelectItem(view: View, selected: Boolean) {
        if (selected) {
            DisplayUtil.setViewVisibility(view, View.VISIBLE)
            editModeSelectAnimator(view)
        } else {
            DisplayUtil.setViewVisibility(view, View.GONE)
        }
    }

    fun editModeSelectAnimator(view: View) {
        ObjectAnimator.ofFloat(view,"alpha",0f, 1f).setDuration(200).start()
    }

    fun editMode() {
        mIsEditMode = !mIsEditMode

        if (mPhotoPageTouchLienter != null) {
            mPhotoPageTouchLienter?.onEditModeChanged(mIsEditMode)
        }
    }

    fun cancelEditMode() {
        mIsEditMode = false
        resetSelectList()

        if (mPhotoPageTouchLienter != null) {
            mPhotoPageTouchLienter?.onEditModeChanged(mIsEditMode)
            mPhotoPageTouchLienter?.onEditModeSelectItem(mSelectedList!!, mCurPostion)
        }


    }

    override fun getItemCount(): Int {
        return mWishPhotoList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mWalkAroundIv: WalkAroundImageView
        var mSelectWalkAroundBg: FrameLayout
        var mShowYearsTv: CustomFontTextView
        var mShowTimeTv: CustomFontTextView
        init {
            mWalkAroundIv = itemView.findViewById(R.id.walk_around_iv)
            mSelectWalkAroundBg = itemView.findViewById(R.id.select_walk_around)
            mShowYearsTv = itemView.findViewById(R.id.wish_photo_show_years)
            mShowTimeTv = itemView.findViewById(R.id.wish_photo_show_month)
        }
    }

    interface IPhotoPageTouchLienter {
        fun onClickPhotoPage(position: Int, filePath: String, holder: PageViewHolder)
        fun onEditModeChanged(isEditMode: Boolean)
        fun onEditModeSelectItem(selectedList: BooleanArray, curPosition: Int)
    }

}