package com.dqc.puppymoney.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishPhotoAlbumBean
import com.dqc.puppymoney.bean.WishToChooseBean
import com.dqc.puppymoney.dao.WishListDao
import com.dqc.puppymoney.database.AppDatabase
import com.dqc.puppymoney.ui.view.PhotoAlbumPreviewListLayout
import com.dqc.puppymoney.util.FileUtil

class WishPhotoAlbumAdapter: RecyclerView.Adapter<WishPhotoAlbumAdapter.PhotoAlbumViewHolder>() {

    private var mWishList: ArrayList<WishToChooseBean> = ArrayList()
    private var mIOnClickCallBack: IOnClickCallBack? = null
    private var mWishPhotoList = ArrayList<WishPhotoAlbumBean>()
    private var mWishListDao: WishListDao? = null

    fun setOnClickCallBack(onClickCallBack: IOnClickCallBack) {
        mIOnClickCallBack = onClickCallBack
    }

    fun setWishList(wishList: ArrayList<WishToChooseBean>) {
        mWishList = wishList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAlbumViewHolder {
        mWishListDao = AppDatabase.getInstance(parent.context).getWishListDao()

        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.fg_wish_photo_album_item, parent, false)
        return PhotoAlbumViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: PhotoAlbumViewHolder, position: Int) {
        holder.photoAlbumTitle.text = mWishList.get(position).wishText

        FileUtil.fileNotExistHandle(mWishListDao!!)
        val photoAlbumList = mWishListDao?.getPhotoAlbumListLimit(mWishList.get(position).wishText)
        mWishPhotoList.clear()
        mWishPhotoList.addAll(photoAlbumList!!)
        holder.wishPhotolist.setWishPhotoList(mWishPhotoList)

        holder.itemView.setOnClickListener {
            if (mIOnClickCallBack != null) {
                mIOnClickCallBack?.onItemClick(position, holder)
            }
        }

    }

    override fun getItemCount(): Int {
        return mWishList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class PhotoAlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var photoAlbumTitle: TextView
        var wishPhotolist: PhotoAlbumPreviewListLayout
        init {
            photoAlbumTitle = itemView.findViewById(R.id.wish_photo_album_title)
            wishPhotolist = itemView.findViewById(R.id.wish_photo_album_preview_list)
        }
    }

    interface IOnClickCallBack {
        fun onItemClick(position: Int, holder: PhotoAlbumViewHolder)
    }
}