package com.dqc.puppymoney.ui.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.DateBean
import com.dqc.puppymoney.interfaces.IDateCallback
import com.dqc.puppymoney.ui.adapter.WishFragmentAdapter
import com.dqc.puppymoney.ui.fragment.SuccessDiaryFragment
import com.dqc.puppymoney.ui.fragment.WishListFragment
import com.dqc.puppymoney.ui.fragment.WishPhotoAlbumFragment
import com.dqc.puppymoney.util.DateUpdate
import com.dqc.puppymoney.util.DisplayUtil
import com.dqc.puppymoney.util.SunRiseSetJ
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.plattysoft.leonids.ParticleSystem
import com.plattysoft.leonids.modifiers.AlphaModifier
import com.plattysoft.leonids.modifiers.ScaleModifier
import kotlinx.android.synthetic.main.activity_demo.*
import kotlinx.android.synthetic.main.activity_wish.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList


class WishActivity : BaseActivity(), RadioGroup.OnCheckedChangeListener {

    private val TAG = "WishActivity"
    private var mFragmentList: ArrayList<Fragment> ?= null
    private lateinit var mWishListFragment: WishListFragment
    private lateinit var mWishPhotoAlbumFragment: WishPhotoAlbumFragment
    private lateinit var mSuccessDiaryFragment: SuccessDiaryFragment
    private var mPreviousPosition: Int = 0
    private var mTipList: ArrayList<String> ?= null
    private var mDateUpdate: DateUpdate? = null
    private var mCurrentMillis: Long = 0L
    private var timer: Timer? = null
    private var particleEffectsTimer: Timer? = null
    private var mParticleSystem: ParticleSystem? = null
    private var mShotIconIdList: IntArray? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish)

        initView()
        initDateView()
        initParticleEffects()

//        XXPermissions.with(this)
//                .permission(Permission.ACCESS_FINE_LOCATION).request(object : OnPermissionCallback {
//                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
//                        getLocation()
//                    }
//
//                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
//
//                    }
//
//                })
    }

    fun getLocation() { // 获取Location通过LocationManger获取！
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        //添加权限检查

        //添加权限检查
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //设置每一秒获取一次location信息
        //设置每一秒获取一次location信息
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,  //GPS定位提供者
                1000, 1f,  //位置间隔为1米
                //位置监听器
                object : LocationListener {
                    //GPS定位信息发生改变时触发，用于更新位置信息
                    override fun onLocationChanged(location: Location) {
                        //GPS信息发生改变时，更新位置
                        locationUpdates(location)
                    }

                    //位置状态发生改变时触发
                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                    //定位提供者启动时触发
                    override fun onProviderEnabled(provider: String) {}

                    //定位提供者关闭时触发
                    override fun onProviderDisabled(provider: String) {}
                })
        //从GPS获取最新的定位信息
        //从GPS获取最新的定位信息
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        locationUpdates(location)
    }

    private fun locationUpdates(location: Location?) {
        Toast.makeText(this, "Location ${location?.latitude} ${location?.longitude}", Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        wish_annual_schedule.startTheSunRotateAnim()
    }

    private fun initParticleEffects() {

        particleEffectsTimer = Timer()
        particleEffectsTimer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread({
                    var layoutParams: RelativeLayout.LayoutParams = particle_view.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.bottomMargin = Random().nextInt(DisplayUtil.dip2px(this@WishActivity, 240)
                            .toInt())
                    particle_view.layoutParams = layoutParams
                    var iconId = getRandomIconId()
                    mParticleSystem = ParticleSystem(this@WishActivity, 2, iconId, 1000 * 30)
                    mParticleSystem?.setAcceleration(0.000013f, 180)
                    mParticleSystem?.setSpeedByComponentsRange(0.05f, 0.1f, -0.02f, -0.05f)
                    mParticleSystem?.setScaleRange(0.4f, 1f)
                    mParticleSystem?.addModifier(AlphaModifier(255, 0, 1000, 1000 * 10))
                    mParticleSystem?.addModifier(ScaleModifier(0.5f, 0.8f, 0, 1000 * 10))
                    mParticleSystem?.oneShot(particle_view, 1)
                })
            }

        }, 100, 1600)
    }

    private fun getRandomIconId(): Int {
        mShotIconIdList = intArrayOf(R.drawable.round_ic,
            R.drawable.round_ic_001,
            R.drawable.round_ic_002,
            R.drawable.round_ic_003,
            R.drawable.round_ic_004,
            R.drawable.round_ic_005,
            R.drawable.round_ic_006)
        val nextInt = Random().nextInt(mShotIconIdList!!.size)
        return mShotIconIdList!!.get(nextInt)
    }

    private fun initDateView() {
        wish_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position - mPreviousPosition > 0) {
                    scrollUpAnimation(position)
                } else if (position - mPreviousPosition < 0) {
                    scrollDownAnimation(position)
                }
                if (mPreviousPosition != position) {
                    radioButtonAnimationCompose(mPreviousPosition, position)
                }
                mPreviousPosition = position

                Log.d("WishActivity", " position " + position)
            }
        })

        wish_date_layout.setOnClickListener {
            if (mCurrentMillis == 0L) {
                mCurrentMillis = SystemClock.elapsedRealtime()
            }

            if (wish_time_tv.visibility == View.GONE) {
                wish_time_tv.visibility = View.VISIBLE
                wish_sun_status_tv.visibility = View.VISIBLE
                showTimeAnimator()
                timedClose()
            } else if (wish_time_tv.visibility == View.VISIBLE) {
                if (SystemClock.elapsedRealtime() - mCurrentMillis > 300) {
                    hideTimeAnimator()
                    mCurrentMillis = 0
                }
            }

        }

        mDateUpdate = DateUpdate()
        mDateUpdate?.sendEmptyMessage(DateUpdate.HANDLER_WHAT)
        mDateUpdate?.setDataCallBack(object : IDateCallback {
            val stringBuffer = StringBuffer()
            override fun onYearsRefresh(dateBean: DateBean) {
                wish_year_tv.text = "${dateBean.mCurrentYear}"
            }

            override fun onMonthRefresh(dateBean: DateBean) {
                stringBuffer.setLength(0)
                stringBuffer.append(DisplayUtil.monthSimple(dateBean.mCurrentMonth))
                        .append(" ")
                        .append(dateBean.mCurrentDaysOfMonth)
                        .append(" ")
                        .append(DisplayUtil.weekDayChinese(dateBean.mCurrentDaysOfWeek))
                wish_day_tv.setText(stringBuffer.toString())
            }

            override fun onTimeReferesh(dateBean: DateBean) {
                stringBuffer.setLength(0)
                stringBuffer.append(DisplayUtil.numberAddZero(dateBean.mCurrentHours))
                        .append(":")
                        .append(DisplayUtil.numberAddZero(dateBean.mCurrentMinute))
                        .append(":")
                        .append(DisplayUtil.numberAddZero(dateBean.mCurrentSecond))
                wish_time_tv.setText(stringBuffer.toString())
            }

        })
    }

    fun timedClose() {
        var time = 3
        if (timer != null) {
            timer?.cancel()
        }
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (time <= 0) {
                    runOnUiThread({
                        hideTimeAnimator()
                        this.cancel()
                    })
                }
                time--
            }
        }, 0, 1000)
    }

    fun showTimeAnimator() {
        ObjectAnimator.ofFloat(wish_time_tv, "translationX", -DisplayUtil.dip2px(this, 12), 0f)
                .setDuration(200)
                .start()
        ObjectAnimator.ofFloat(wish_time_tv, "alpha", 0f, 1f)
                .setDuration(200)
                .start()

        ObjectAnimator.ofFloat(wish_sun_status_tv, "translationY", -DisplayUtil.dip2px(this, 12), 0f)
            .setDuration(200)
            .start()
        ObjectAnimator.ofFloat(wish_sun_status_tv, "alpha", 0f, 1f)
            .setDuration(200)
            .start()

        var rise = "日出${SunRiseSetJ.getSunrise(BigDecimal(118.72),BigDecimal(32.00500), Date())}"
        var set = "日落${SunRiseSetJ.getSunset(BigDecimal(118.72),BigDecimal(32.00500), Date())}"

        wish_sun_status_tv.setText(rise + "\t\t" +set)

    }

    fun hideTimeAnimator() {
        ObjectAnimator.ofFloat(wish_time_tv, "translationX", 0f, -DisplayUtil.dip2px(this, 12))
                .setDuration(200)
                .start()
        var alpha = ObjectAnimator.ofFloat(wish_time_tv, "alpha", 1f, 0f)
        alpha.duration = 200
        alpha.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                wish_time_tv.visibility = View.GONE
            }
        })
        alpha.start()

        ObjectAnimator.ofFloat(wish_sun_status_tv, "translationY", 0f, -DisplayUtil.dip2px(this, 12))
            .setDuration(200)
            .start()
        var alphaSun = ObjectAnimator.ofFloat(wish_sun_status_tv, "alpha", 1f, 0f)
        alphaSun.duration = 200
        alphaSun.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                wish_sun_status_tv.visibility = View.GONE
            }
        })
        alphaSun.start()
    }

    fun radioButtonAnimationCompose(previousPosition: Int, position: Int) {
        val previousPositionView = findRadioButtonbyPosition(previousPosition)!! as RadioButton
        val positionView = findRadioButtonbyPosition(position)!! as RadioButton

        previousPositionView.setCompoundDrawables(null, null, null, null)

        val pointDrawable = getDrawable(R.drawable.wish_selected_point_ic)
//        var lineDrawable = getDrawable(R.drawable.wish_select_bottom_line)
        pointDrawable?.setBounds(0, 0, pointDrawable.minimumWidth, pointDrawable.minimumHeight)
//        var lineWidth =  positionView.width
//        if (lineWidth == 0) {
//            lineWidth = DisplayUtil.dip2px(applicationContext, 46).toInt()
//        }
//        Log.d("WishActivity", " lineWidth " + lineWidth)
//        lineDrawable?.setBounds(0, 0, lineWidth, DisplayUtil.dip2px(this, 2).toInt())

        positionView.setCompoundDrawables(pointDrawable, null, null, null)

        radioButtonUnSelectedAnimation(previousPositionView)
        radioButtonSelectedAnimation(positionView)
    }

    fun findRadioButtonbyPosition(position: Int): View? {
        if (position == 0) {
            return wish_list_rb
        } else if (position == 1) {
            return wish_photo_album_rb
        } else if (position == 2) {
            return wish_success_diary_rb
        }
        return null
    }

    fun radioButtonSelectedAnimation(view: View) {
        ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.27f).setDuration(200).start()
        ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.27f).setDuration(200).start()
        ObjectAnimator.ofArgb(view as RadioButton, "textColor", 0x7FD4D4D4, 0xCCFFFFFF.toInt()).setDuration(200).start()
    }

    fun radioButtonUnSelectedAnimation(view: View) {
        ObjectAnimator.ofFloat(view, "scaleX", 1f).setDuration(200).start()
        ObjectAnimator.ofFloat(view, "scaleY", 1f).setDuration(200).start()
        ObjectAnimator.ofArgb(view as RadioButton, "textColor", 0xCCFFFFFF.toInt(), 0x7FD4D4D4).setDuration(200).start()
    }

    fun scrollUpAnimation(textPosition: Int) {
        ObjectAnimator.ofFloat(wish_page_tips_tv, "alpha", 1f, 0f)
                .setDuration(200)
                .start()
        var translation = ObjectAnimator.ofFloat(
                wish_page_tips_tv, "translationY", 0f, -DisplayUtil.dip2px(
                this,
                16
        ))
        translation.duration = 200
        translation.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                wish_page_tips_tv.text = mTipList?.get(textPosition)
                ObjectAnimator.ofFloat(wish_page_tips_tv, "alpha", 0f, 1f)
                        .setDuration(200)
                        .start()
                var translatio = ObjectAnimator.ofFloat(
                        wish_page_tips_tv, "translationY", DisplayUtil.dip2px(
                        this@WishActivity.applicationContext,
                        16
                ), 0f
                )
                translatio.duration = 200
                translatio.start()
            }
        })
        translation.start()
    }

    private fun scrollDownAnimation(textPosition: Int) {
        ObjectAnimator.ofFloat(wish_page_tips_tv, "alpha", 1f, 0f)
                .setDuration(200)
                .start()
        var translation = ObjectAnimator.ofFloat(
                wish_page_tips_tv, "translationY", 0f, DisplayUtil.dip2px(
                this,
                16
        )
        )
        translation.duration = 200
        translation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {

                wish_page_tips_tv.text = mTipList?.get(textPosition)
                ObjectAnimator.ofFloat(wish_page_tips_tv, "alpha", 0f, 1f)
                        .setDuration(200)
                        .start()
                var translatio = ObjectAnimator.ofFloat(
                        wish_page_tips_tv, "translationY", -DisplayUtil.dip2px(
                        this@WishActivity.applicationContext,
                        16
                ), 0f)
                translatio.duration = 200
                translatio.start()
            }
        })
        translation.start()
    }

    private fun initView() {

        mFragmentList = ArrayList()
        mTipList = ArrayList()
        mWishListFragment = WishListFragment()
        mWishPhotoAlbumFragment = WishPhotoAlbumFragment()
        mSuccessDiaryFragment = SuccessDiaryFragment()

        mWishListFragment.setWishPhotoAlbumFragment(mWishPhotoAlbumFragment)

        mFragmentList?.add(mWishListFragment)
        mFragmentList?.add(mWishPhotoAlbumFragment)
        mFragmentList?.add(mSuccessDiaryFragment)

        mTipList?.add(applicationContext.getString(R.string.wish_page_wish_tips_text))
        mTipList?.add(applicationContext.getString(R.string.wish_page_photo_album))
        mTipList?.add(applicationContext.getString(R.string.wish_page_success_diary_text))

        //取消滚动结束后的动画
        val child: View = wish_viewpager.getChildAt(0)
        if (child is RecyclerView) {
            child.setOverScrollMode(View.OVER_SCROLL_NEVER)
        }
        wish_radio_group.setOnCheckedChangeListener(this)
        wish_viewpager.orientation = ViewPager2.ORIENTATION_VERTICAL
        wish_viewpager.adapter = WishFragmentAdapter(this, mFragmentList!!)
        radioButtonSelectedAnimation(wish_list_rb)

        val pointDrawable = getDrawable(R.drawable.wish_selected_point_ic)
//        var lineDrawable = getDrawable(R.drawable.wish_select_bottom_line)
        pointDrawable?.setBounds(0, 0, pointDrawable.minimumWidth, pointDrawable.minimumHeight)
//        var lineWidth =  wish_list_rb.measuredWidth
//        if (lineWidth == 0) {
//            lineWidth = DisplayUtil.dip2px(applicationContext, 46).toInt()
//        }
//        Log.d("WishActivity", " lineWidth " + lineWidth)
//        lineDrawable?.setBounds(0, 0, lineWidth, DisplayUtil.dip2px(this, 2).toInt())
        wish_list_rb.setCompoundDrawables(pointDrawable, null, null, null)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        var current = 0
        Log.d("CheckedChanged", " checkedId " + checkedId)
        when(checkedId) {
            R.id.wish_list_rb -> {
                wish_list_rb.isChecked = false
                current = 0
            }
            R.id.wish_photo_album_rb -> {
                wish_photo_album_rb.isChecked = false
                current = 1
            }
            R.id.wish_success_diary_rb -> {
                wish_success_diary_rb.isChecked = false
                current = 2
            }
        }
        wish_viewpager.setCurrentItem(current)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        wish_annual_schedule.pauseTheSunRotateAnim()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (particleEffectsTimer != null) {
            particleEffectsTimer?.cancel()
            particleEffectsTimer = null
        }

        if (mParticleSystem != null) {
            mParticleSystem?.cancel()
            mParticleSystem = null
        }

        if (mFragmentList != null) {
            mFragmentList?.clear()
            mFragmentList = null
        }

        if (mTipList != null) {
            mTipList?.clear()
            mTipList = null
        }

        wish_annual_schedule.pauseTheSunRotateAnim()
        mDateUpdate?.removeMessages(DateUpdate.HANDLER_WHAT)
    }

}