package com.dqc.puppymoney.ui.view

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishPhotoAlbumBean
import com.dqc.puppymoney.util.DisplayUtil
import java.io.File

class PhotoAlbumPreviewListLayout(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var mWishPhotoList = ArrayList<WishPhotoAlbumBean>()

    fun setWishPhotoList(wishPhotoList: ArrayList<WishPhotoAlbumBean>) {
        mWishPhotoList = wishPhotoList
        initView()
    }

    private fun initView() {
        removeAllViews()
        for (i in 0..mWishPhotoList.size - 1) {
            Glide.with(context)
                    .load(File(mWishPhotoList.get(i).imgPath))
                    .into(createRoundImageView())
        }

        if (mWishPhotoList.size < 5) {
            Glide.with(context)
                    .load(R.drawable.add_image_ic)
                    .into(createRoundImageView())
        }
    }

    fun createRoundImageView(): RoundImageView {
        var roundImageView = RoundImageView(context, null)
        roundImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        roundImageView.setFilletRadius(DisplayUtil.dip2px(context!!, 6).toInt())

        addView(roundImageView)

        val layoutParams: LayoutParams = roundImageView.layoutParams as LayoutParams
        layoutParams.width = DisplayUtil.dip2px(context, 42).toInt()
        layoutParams.height = DisplayUtil.dip2px(context, 42).toInt()
        layoutParams.leftMargin = DisplayUtil.dip2px(context, 12).toInt()
        return roundImageView
    }

}