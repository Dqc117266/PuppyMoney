package com.dqc.puppymoney.ui.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishToChooseBean
import com.dqc.puppymoney.dao.WishListDao
import com.dqc.puppymoney.database.AppDatabase
import com.dqc.puppymoney.interfaces.TouchCallBack
import com.dqc.puppymoney.interfaces.WishPhotoNeedRefrshCallBack
import com.dqc.puppymoney.ui.view.CustomFontTextView
import com.dqc.puppymoney.util.DisplayUtil
import java.util.*
import kotlin.collections.ArrayList

class WishListAdapter(context: Context?): RecyclerView.Adapter<RecyclerView.ViewHolder>(), TouchCallBack {

    private var mWishList: ArrayList<WishToChooseBean> = ArrayList()
    private var doubleDownClickTime = 0L
    private var mTouchDownTime = 0L
    private var mTouchUpTime = 0L
    private var mAnimatorSet: AnimatorSet? = null
    private var mWishListDao: WishListDao? = null
    private var mContext: Context = context!!
    private var mWishPhotoNeedRefrshCallBack: WishPhotoNeedRefrshCallBack? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var mIsFirstGetItemHeight = true
    private var chengedHeight = 0

    fun setWishPhotoNeedRefreshCallBack(wishPhotoNeedRefrshCallBack: WishPhotoNeedRefrshCallBack) {
        mWishPhotoNeedRefrshCallBack = wishPhotoNeedRefrshCallBack
    }

    fun setRecyclerViewLayoutManager(layoutManager: LinearLayoutManager) {
        mLayoutManager = layoutManager
    }

    fun setWishList(wishList: ArrayList<WishToChooseBean>) {
        mWishList = wishList
        notifyDataSetChanged()
    }

    fun setWishListDao(wishListDao: WishListDao) {
        mWishListDao = wishListDao
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var inflater = LayoutInflater.from(parent.context).inflate(R.layout.fg_wish_list_rv_wish_item, parent, false)
        return WishViewHolder(inflater)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is WishViewHolder) {

            if (position < 3) {
//                holder.wishText.scaleX = 1.25f
//                holder.wishText.scaleY = 1.25f
//                wishItemBiggerPosAnimtor(holder.wishText)

//                holder.wishText.post {
//                    holder.wishText.translationX = ((holder.wishText.width * 1.25f) - holder.wishText.width) / 2
//                    chengedHeight = holder.itemView.height
//                }

            }
            holder.wishText.post {
                chengedHeight = holder.wishText.height
                Log.d("ChangedHeight", " $chengedHeight")

            }

            if (mIsFirstGetItemHeight) {
                holder.itemView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (mWishPhotoNeedRefrshCallBack != null) {
                            mWishPhotoNeedRefrshCallBack?.onGetViewHeight(holder.itemView.height)
                        }
                        Log.d("DDDD", " ${holder.itemView.height} position ${position}")
                        holder.itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    }
                })
                mIsFirstGetItemHeight = false
            }

            holder.wishText.setText(mWishList.get(position).wishText)
            holder.itemView.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    Log.d("click", " event ")
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mTouchDownTime = SystemClock.elapsedRealtime()
                            Log.d("click", " ACTION_DOWN ")
                            startMoveTipAnimator(holder.moveTipIv, holder.content, true)
                        }

                        MotionEvent.ACTION_UP -> {
                            mTouchUpTime = SystemClock.elapsedRealtime()
                            Log.d("click", " ACTION_UP ")
                            startMoveTipAnimator(holder.moveTipIv, holder.content, false)
                        }

                        MotionEvent.ACTION_CANCEL -> {
                            Log.d("click", " ACTION_CANCEL ")
                            startMoveTipAnimator(holder.moveTipIv, holder.content, false)
                        }
                    }
                    return true
                }
            })
        }
    }
    private fun preventKeyDownFast(): Boolean {
        return if (SystemClock.elapsedRealtime() - doubleDownClickTime < 200) { //防止按键过快
            true
        } else {
            doubleDownClickTime = SystemClock.elapsedRealtime()
            false
        }
    }

    private fun startMoveTipAnimator(view: View, view2: View, isShow: Boolean) {
        var startValue = 0f
        var endValue = 0f

        if (isShow) {
            DisplayUtil.setViewVisibility(view, View.VISIBLE)
            DisplayUtil.setViewVisibility(view2, View.VISIBLE)
            endValue = 1f
        } else {
            startValue = 1f
        }

        if (mTouchDownTime - mTouchUpTime > 200
                || mTouchUpTime - mTouchDownTime > 200) {
            val alpha = ObjectAnimator.ofFloat(view, "alpha", startValue, endValue)
            alpha.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    if (!isShow) {
                        DisplayUtil.setViewVisibility(view, View.GONE)
                        DisplayUtil.setViewVisibility(view2, View.GONE)
                    }
                }
            })
            val alpha2 = ObjectAnimator.ofFloat(view2, "alpha", startValue, endValue)
            var scaleY = ObjectAnimator.ofFloat(view2, "scaleY", startValue, endValue)

            if (mAnimatorSet != null) {
                mAnimatorSet?.cancel()
                mAnimatorSet = null
            }

            mAnimatorSet = AnimatorSet()
            mAnimatorSet?.setDuration(400)
            mAnimatorSet?.play(alpha)?.with(alpha2)?.with(scaleY)
            mAnimatorSet?.start()
        } else {
            if (isShow) {
                DisplayUtil.setViewVisibility(view, View.VISIBLE)
                DisplayUtil.setViewVisibility(view2, View.VISIBLE)
            } else {
                DisplayUtil.setViewVisibility(view, View.GONE)
                DisplayUtil.setViewVisibility(view2, View.GONE)
            }
        }
    }

    override fun getItemCount(): Int {
        return mWishList.size
    }

    fun getChineseNumbers(position: Int) : Char {
        return charArrayOf('一', '二', '三', '四', '五', '六', '七', '八', '九', '十')[position]
    }

    class WishViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var wishText: TextView
        var moveTipIv: ImageView
        var content: View

        init {
            wishText = view.findViewById(R.id.fg_wish_list_text_tv)
            moveTipIv = view.findViewById(R.id.move_tip_iv)
            content = view.findViewById(R.id.content_bg)
        }
    }

    class SplitLineViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var wentedText: TextView
        init {
            wentedText = view.findViewById(R.id.wish_list_wented_tips_tv)
        }
    }

    private fun swap(list: ArrayList<WishToChooseBean>, fromPosition: Int, toPosition: Int) {
        var temp = list.get(fromPosition)
        list.set(fromPosition, list.get(toPosition))
        list.set(toPosition, temp)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Log.d("WishListAdapter", "fromPosition ${fromPosition}  toPosition${toPosition}")
//        swap(mWishList, fromPosition, toPosition)
        val removeAt = mWishList.removeAt(fromPosition)
        mWishList.add(toPosition, removeAt)
        notifyItemMoved(fromPosition, toPosition)

        mWishList.get(fromPosition).index = fromPosition
        mWishList.get(toPosition).index = toPosition

//        if (mLayoutManager != null) {
//            var fromView = mLayoutManager?.findViewByPosition(fromPosition)?.findViewById(R.id.fg_wish_list_text_tv) as CustomFontTextView
//            var toView = mLayoutManager?.findViewByPosition(toPosition)?.findViewById(R.id.fg_wish_list_text_tv) as CustomFontTextView
//            Log.d("WishListAdapter", "${fromView.findViewById<CustomFontTextView>(R.id.fg_wish_list_text_tv).text}  ${toView.findViewById<CustomFontTextView>(R.id.fg_wish_list_text_tv).text}")
//            if (fromPosition < 3 && toPosition >= 3) {
//                wishItemBiggerPosAnimtor(toView)
//                wishItemSmallerPosAnimtor(fromView)
//            } else if (fromPosition >= 3 && toPosition < 3) {
//                wishItemBiggerPosAnimtor(fromView)
//                wishItemSmallerPosAnimtor(toView)
//            }
//        }
        for (i in 0..mWishList.size - 1) {
            Log.d("onMove", " ${mWishList.get(i).wishText}  ${mWishList.get(i).index}")
//            mWishList.get(i).index = i
        }
        Log.d("onMove", "++++++================================++++++")

    }

    private fun wishItemSmallerPosAnimtor(view: View) {
        var endValue = 1f
        var translationValue = 0f
        ObjectAnimator.ofFloat(view, "scaleX", endValue).setDuration(200).start()
        ObjectAnimator.ofFloat(view, "scaleY", endValue).setDuration(200).start()
        ObjectAnimator.ofFloat(view, "translationX", translationValue).setDuration(200).start()
    }

    private fun wishItemBiggerPosAnimtor(view: View) {
        var startValue = 1f
        var endValue = 1.25f
        view.post {
            var translationValue = ((view.width * 1.25f) - view.width) / 2
            ObjectAnimator.ofFloat(view, "scaleX", startValue, endValue).setDuration(200).start()
            ObjectAnimator.ofFloat(view, "scaleY", startValue, endValue).setDuration(200).start()
            ObjectAnimator.ofFloat(view, "translationX", translationValue).setDuration(200).start()
        }

    }

    override fun onClearView() {

        Log.d("onClearView", " onClearview")
        for (i in 0..mWishList.size - 1) {
            Log.d("onClearView", " ${mWishList.get(i).wishText}  ${mWishList.get(i).index}")
            mWishList.get(i).index = i
        }

        val wishListDao = AppDatabase.getInstance(mContext).getWishListDao()
        wishListDao.updateWishList(mWishList)

        if (mWishPhotoNeedRefrshCallBack != null) {
            mWishPhotoNeedRefrshCallBack?.onRefresh()
        }

    }
    override fun onItemDelete(position: Int) {
        mWishList.removeAt(position)

        notifyItemRemoved(position)
    }

}