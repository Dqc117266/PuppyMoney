package com.dqc.puppymoney.ui.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishToChooseBean
import com.dqc.puppymoney.database.AppDatabase
import com.dqc.puppymoney.ui.activity.WishPhotoAlbumActivity
import com.dqc.puppymoney.ui.adapter.WishPhotoAlbumAdapter
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class WishPhotoAlbumFragment : Fragment(), WishPhotoAlbumAdapter.IOnClickCallBack {

    private lateinit var mPhotoAlbumRecyclerView: RecyclerView
    private lateinit var mPhotoAlbumAdapter: WishPhotoAlbumAdapter
    private var mMostWantedWishList: ArrayList<WishToChooseBean> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.fragment_wish_photo_album, container, false)

        mPhotoAlbumRecyclerView = inflate.findViewById(R.id.wish_photo_album_rv)
        mPhotoAlbumRecyclerView.layoutManager = LinearLayoutManager(context)

        mPhotoAlbumAdapter = WishPhotoAlbumAdapter()
        mPhotoAlbumAdapter.setOnClickCallBack(this)
        mPhotoAlbumRecyclerView.adapter = mPhotoAlbumAdapter

        updateWantedWishList()

        return inflate
    }

    fun updateWantedWishList() {
        if (context != null) {
            val wishList = AppDatabase.getInstance(context!!).getWishListDao().getMostWantedWishList()
            mMostWantedWishList.clear()
            mMostWantedWishList.addAll(wishList)
            mPhotoAlbumAdapter.setWishList(mMostWantedWishList)
        }
    }

    override fun onResume() {
        super.onResume()
        mPhotoAlbumAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int, holder: WishPhotoAlbumAdapter.PhotoAlbumViewHolder) {
        if (mMostWantedWishList.size >= 0
                && mMostWantedWishList.size <= 3) {
            initPermission(mMostWantedWishList.get(position).wishText, holder)
        }
    }


    private fun initPermission(wishText: String, holder: WishPhotoAlbumAdapter.PhotoAlbumViewHolder) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            XXPermissions.with(this)
                    .permission(Permission.CAMERA)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                            if (all) {
                                startWishPhotoAlbumActivity(wishText, holder)
                            }
                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                            if (never) {
                                Toast.makeText(activity, R.string.wish_photo_album_permanent_deny_permissions, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(activity, R.string.wish_photo_album_obtain_failure_permissions, Toast.LENGTH_LONG).show()
                            }
                        }

                    })
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M
                && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            XXPermissions.with(this)
                    .permission(Permission.CAMERA)
                    .permission(Permission.Group.STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                            if (all) {
                                startWishPhotoAlbumActivity(wishText, holder)
                            }
                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                            if (never) {
                                Toast.makeText(activity, R.string.wish_photo_album_permanent_deny_permissions, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(activity, R.string.wish_photo_album_obtain_failure_permissions, Toast.LENGTH_LONG).show()
                            }
                        }

                    })
        } else {
            startWishPhotoAlbumActivity(wishText, holder)
        }
    }

    fun startWishPhotoAlbumActivity(wishText: String, holder: WishPhotoAlbumAdapter.PhotoAlbumViewHolder) {
        var intent = Intent(activity, WishPhotoAlbumActivity::class.java)
        intent.putExtra("wish_text", wishText)
        startActivity(intent)
        Log.d("StartWishPhoto", " ${holder != null}")
        activity?.overridePendingTransition(R.anim.open_wish_photo_in, R.anim.open_wish_photo_out)
//        startActivity(intent,
//                makeSceneTransitionAnimation(activity, holder.itemView, "share").toBundle())
    }

}