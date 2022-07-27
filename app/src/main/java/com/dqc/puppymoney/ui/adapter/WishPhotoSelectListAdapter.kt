package com.dqc.puppymoney.ui.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishPhotoAlbumBean
import com.dqc.puppymoney.ui.view.RoundImageView
import java.io.File

class WishPhotoSelectListAdapter:
    RecyclerView.Adapter<WishPhotoSelectListAdapter.SelectListViewHolder>() {
    private var mSelPosition = 0
    private var mPreviousPosition = 0
    private var temp = -1
    private var mIClickItemCallback: IClickItemCallback? = null
    private var mWishPhotoList = ArrayList<WishPhotoAlbumBean>()
    private var mSelectedList: BooleanArray? = null
    private var mContext: Context? = null
    private var mIsEditMode: Boolean = false

    fun setWishPhotoList(wishPhotoList: ArrayList<WishPhotoAlbumBean>) {
        mWishPhotoList = wishPhotoList
        notifyDataSetChanged()
    }

    fun setIClickItemCallback(iClickItemCallback: IClickItemCallback) {
        mIClickItemCallback = iClickItemCallback
    }

    fun setEditModeSelect(isEditMode: Boolean, selectedList: BooleanArray?) {
        mIsEditMode = isEditMode
        mSelectedList = selectedList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectListViewHolder {
        mContext = parent.context
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.wish_photo_select_list_item, parent, false)
        return SelectListViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: SelectListViewHolder, position: Int) {
        holder.itemView.isSelected = holder.layoutPosition == mSelPosition
        isSelectdShow(holder)

        Glide.with(mContext)
                .load(File(mWishPhotoList.get(position).imgPath))
                .asBitmap()
                .skipMemoryCache(true)
                .dontAnimate()
                .into(object :SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                        holder.mSelectIv.setImageBitmap(resource)
                    }
                })

        if (mSelectedList != null
                && mSelectedList?.size == mWishPhotoList.size) {
            if (mSelectedList!!.get(position)) {
                holder.mSelectPreView.visibility = View.VISIBLE
            } else {
                holder.mSelectPreView.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            isSelectdShow(holder)

            holder.itemView.isSelected = true
            temp = mSelPosition
            mSelPosition = holder.layoutPosition

            if (mIClickItemCallback != null) {
                mIClickItemCallback?.onItemClick(position, holder)
            }

            notifyItemChanged(temp)
        }

        mPreviousPosition = position
    }

    fun selectItem(position: Int) {
        mSelPosition = position
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun isSelectdShow(holder: SelectListViewHolder) {
        if (holder.itemView.isSelected) {
            holder.mSelectFrameIv.visibility = View.VISIBLE
            selectAnimationStart(holder.mSelectFrameIv)
            Log.d("WishPhotoSelectList", " isSelected")
        } else {
            holder.mSelectFrameIv.visibility = View.GONE
        }
    }

    private fun selectAnimationStart(view: View) {
        var objectAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.75f, 1f)
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.duration = 200
        objectAnimator.start()

        objectAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.75f, 1f)
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.duration = 200
        objectAnimator.start()
    }

    override fun getItemCount(): Int {
        return mWishPhotoList.size
    }

    class SelectListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mSelectIv: RoundImageView
        var mSelectFrameIv: ImageView
        var mSelectPreView: FrameLayout
        init {
            mSelectIv = itemView.findViewById(R.id.select_iv)
            mSelectFrameIv = itemView.findViewById(R.id.select_frame_iv)
            mSelectPreView = itemView.findViewById(R.id.bottom_select_preview)
        }
    }

    interface IClickItemCallback {
        fun onItemClick(position: Int, itemView: SelectListViewHolder)
    }

}