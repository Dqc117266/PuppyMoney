package com.dqc.puppymoney.ui.fragment

import SimpleItemTouchCallBack
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishToChooseBean
import com.dqc.puppymoney.database.AppDatabase
import com.dqc.puppymoney.interfaces.WishPhotoNeedRefrshCallBack
import com.dqc.puppymoney.ui.adapter.WishListAdapter
import com.dqc.puppymoney.util.DisplayUtil

class WishListFragment : Fragment(), WishPhotoNeedRefrshCallBack {

    private lateinit var mWishListAdapter: WishListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private var mWishList: ArrayList<WishToChooseBean> = ArrayList()
    private var mWishPhotoAlbumFragment: WishPhotoAlbumFragment? = null
    private var mTipLine: LinearLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wish_list, container, false)
        mWishListAdapter = WishListAdapter(context)
        mWishListAdapter.setWishPhotoNeedRefreshCallBack(this)
        mRecyclerView = view.findViewById(R.id.fg_wish_list_rv)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mWishListAdapter.setRecyclerViewLayoutManager(mRecyclerView.layoutManager as LinearLayoutManager)

        var simpleItemTouchCallBack = SimpleItemTouchCallBack(mWishListAdapter)
        simpleItemTouchCallBack.setmSwipeEnable(false)

        var itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)

        mTipLine = view.findViewById(R.id.tip_line)

        Thread({
            val wishList = AppDatabase.getInstance(context!!.applicationContext).getWishListDao().getWishList()
//            val mostWantedwishList = AppDatabase.getInstance(context!!.applicationContext).getWishListDao().getMostWantedWishList(true)
//            mWishList.addAll(mostWantedwishList)
            mWishList.addAll(wishList)
            for (i in 0..mWishList.size - 1) {
                Log.d("WishListFragment", " wish ${mWishList.get(i).wishText} ${mWishList.get(i).isSelected} ${mWishList.get(i).index}")
            }
            activity!!.runOnUiThread({
                mWishListAdapter.setWishList(mWishList)
                mRecyclerView.adapter = mWishListAdapter
            })
        }).start()

        return view
    }

    fun setWishPhotoAlbumFragment(wishPhotoAlbumFragment: WishPhotoAlbumFragment) {
        mWishPhotoAlbumFragment = wishPhotoAlbumFragment
    }

    override fun onRefresh() {
        if (mWishPhotoAlbumFragment != null) {
            mWishPhotoAlbumFragment?.updateWantedWishList()
        }
    }

    override fun onGetViewHeight(height: Int) {
        Log.d("onGetViewHeight", " $height ")
        var marginTopSize = (DisplayUtil.dip2px(context!!, 32) + ((height + DisplayUtil.dip2px(context!!, 16)) * 3) - mTipLine?.height!! + DisplayUtil.dip2px(context!!, 8)).toInt()
        val layoutParams: FrameLayout.LayoutParams = mTipLine?.layoutParams as FrameLayout.LayoutParams
        layoutParams.topMargin = marginTopSize
        mTipLine?.layoutParams = layoutParams
    }

}