package com.dqc.puppymoney.ui.activity

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.WishToChooseBean
import com.dqc.puppymoney.database.AppDatabase
import com.dqc.puppymoney.ui.adapter.WishToChooseAdapter
import com.dqc.puppymoney.util.DisplayUtil
import com.gyf.barlibrary.ImmersionBar
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_wish_to_choose.*

class WishToChooseActivity : AppCompatActivity() {

    private lateinit var mWishToChooseAdapter: WishToChooseAdapter
    private lateinit var immersionBar: ImmersionBar
    private var kv: MMKV? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_to_choose)
        immersionBar = ImmersionBar.with(this)
        immersionBar.statusBarDarkFont(true)
        immersionBar.init()

        kv = MMKV.defaultMMKV()

        mWishToChooseAdapter = WishToChooseAdapter()
        var intent1 = intent
        val stringArrayExtra = intent1.getStringArrayExtra("wish_list")
        mWishToChooseAdapter.setWishList(stringArrayExtra!!)
        wish_to_choose_rv.adapter = mWishToChooseAdapter
        wish_to_choose_rv.layoutManager = LinearLayoutManager(this)
        mWishToChooseAdapter.setLayoutManager(wish_to_choose_rv.layoutManager as LinearLayoutManager)

        mWishToChooseAdapter.setOnWishToChooseCompleteLienter(object :
                WishToChooseAdapter.OnWishToChooseCompleteLienter {
            override fun onWishToChooseComplete(isChooseComplete: Boolean) {
                if (isChooseComplete) {
                    if (wish_to_choose_select_verify.visibility == View.GONE) {
                        wish_to_choose_select_verify.visibility = View.VISIBLE
                    }
                    showSelectVerifyAnimator()
                } else {
                    hideShowSelectVerifyAnimator()
                }
            }
        })

        wish_to_choose_back.setOnClickListener {
            finish()
        }
        wish_to_choose_select_verify.setOnClickListener {

            var selectWishList = ArrayList<WishToChooseBean>()
            var notSelectWishList = ArrayList<WishToChooseBean>()
            val wishList = mWishToChooseAdapter.getWishList()

            for (i in 0..wishList.size - 1) {
                if (wishList.get(i).isSelected) {
                    selectWishList.add(wishList.get(i))
                } else {
                    notSelectWishList.add(wishList.get(i))
                }
            }
            wishList.clear()
            wishList.addAll(selectWishList)
            wishList.addAll(notSelectWishList)
            var index = 0

            for (wish in wishList) {
                wish.index = index
                index++
            }

            Thread {
                val wishListDao = AppDatabase.getInstance(this).getWishListDao()
                val wishList1 = wishListDao.getWishList()
                wishListDao.deleteWishAll(wishList1)
                for (i in 0..wishList.size - 1) {
                    Log.d("WishToChooseActivity", " ${wishList.get(i).wishText}  ${wishList.get(i).isSelected}")
                }
                wishListDao.insertWishAll(wishList)
            }.start()
            kv?.encode("is_first_start", true)
            var intent = Intent(this, WishActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    fun showSelectVerifyAnimator() {
        var translationY = ObjectAnimator.ofFloat(wish_to_choose_select_verify, "translationY", DisplayUtil.dip2px(this, 60) * 2, 0f)
        translationY.duration = 600
        translationY.interpolator = AnticipateOvershootInterpolator()
        translationY.start()

        var k1: Keyframe = Keyframe.ofFloat(0f, 0f)
        var k2: Keyframe = Keyframe.ofFloat(0.75f, 0.3f)
        var k3: Keyframe = Keyframe.ofFloat(1f, 1f)
        var valuesHolder = PropertyValuesHolder.ofKeyframe("alpha", k1, k2, k3)

        var alpha = ObjectAnimator.ofPropertyValuesHolder(wish_to_choose_select_verify, valuesHolder)
        alpha.duration = 600
        alpha.start()
    }

    fun hideShowSelectVerifyAnimator() {
        var translationY = ObjectAnimator.ofFloat(wish_to_choose_select_verify, "translationY", 0f, DisplayUtil.dip2px(this, 60) * 2)
        translationY.duration = 600
        translationY.interpolator = AnticipateOvershootInterpolator()
        translationY.start()

        var alpha = ObjectAnimator.ofFloat(wish_to_choose_select_verify, "alpha", 1f, 0f)
        alpha.duration = 600
        alpha.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }

}