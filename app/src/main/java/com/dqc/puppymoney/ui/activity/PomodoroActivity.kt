package com.dqc.puppymoney.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.PomSendData
import com.dqc.puppymoney.service.PomodoroService
import com.dqc.puppymoney.ui.view.LongClickCancelBar
import com.dqc.puppymoney.util.DisplayUtil
import com.dqc.puppymoney.util.dp2px
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_pomodoro.*

class PomodoroActivity : BaseActivity() {

    private val ANIM_DURATION = 400L
    private var mPomodoroService: PomodoroService? = null
    private var mKV: MMKV? = null

    private var mServideConnect: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var mbinder = service as PomodoroService.PomodoroBinder
            mPomodoroService = mbinder.service
            onServiceConnect()
            Log.d("BindService", " onServiceConnected ")
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)
        mKV = MMKV.defaultMMKV()

        initView()
        bindService()
        registerBroadcast()
    }

    private fun onServiceConnect() {

        var pomCount = mKV?.decodeInt("pom_count")!!
        var minute = mKV?.decodeInt("pom_dartion")!!
        var restMinute = mKV?.decodeInt("pom_rest_dartion")!!

        mPomodoroService?.setPomParam(minute, restMinute, pomCount)

        if (mPomodoroService!!.getIsStart()) {
            pom_control_btn.setImageResource(R.drawable.pom_pause_ic)
        } else {
            if (mPomodoroService!!.getIsCancel()) {
                pom_control_btn.setImageResource(R.drawable.pom_start_ic)
                val minute = mPomodoroService?.mMinute
                pom_view.setTime((minute!!), 0)
                pom_time.setText("${DisplayUtil.numberAddZero(minute!!)}:${DisplayUtil.numberAddZero(0)}")
                return
            }
            pomPauseAnim()
        }

        val countDownMills = mPomodoroService?.getCountDownMills()
        pom_view.setTime((countDownMills!! / 1000 / 60).toInt(), (countDownMills / 1000 % 60).toInt())
        pom_time.setText("${DisplayUtil.numberAddZero((countDownMills!! / 1000 / 60).toInt())}:${DisplayUtil.numberAddZero((countDownMills / 1000 % 60).toInt())}")

    }

    private fun registerBroadcast() {
        var pomReceivr = PomReceiver()
        var intentFilter = IntentFilter()
        intentFilter.addAction("com.dqc.puppymoney")
        registerReceiver(pomReceivr, intentFilter)
    }

    private fun initView() {
        if (mPomodoroService != null) {
            mPomodoroService?.mMinute
        }

        Log.d("pomdoromoServer", " " + (mPomodoroService != null))

        pom_control_btn.setOnClickListener {
            if (mPomodoroService!!.getIsStart()) {
                mPomodoroService!!.pausePomodoro()
                pomPauseAnim()
            } else {
                mPomodoroService!!.startPomodoro()
                pomStartAnim()
            }
        }

        add_pomodoro_close_page.setOnClickListener {
            onBackPressed()
        }

        pom_cancel_btn.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    long_click_cancle.startProgressRiseAnim()
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    long_click_cancle.startProgressFallAnim()
                }
                return true
            }
        })

        long_click_cancle.setOnLongClickCancel(object :LongClickCancelBar.OnLongClickCancel {
            override fun onCancel() {
                mPomodoroService!!.cancelPomodoro()
                pomStartAnim()
            }
        })

    }

    private fun pomPauseAnim() {
        DisplayUtil.setViewVisibility(pom_cancel_btn, View.VISIBLE)
        if (!mPomodoroService!!.getIsStart()) {
            pom_control_btn.setImageResource(R.drawable.pom_start_ic)
        } else {
            pom_control_btn.setImageResource(R.drawable.pom_pause_ic)
        }

        var animSet = AnimatorSet()

        var trans = ObjectAnimator.ofFloat(pom_control_btn, "translationX", 0f, dp2px(45).toFloat())
        var trans1 = ObjectAnimator.ofFloat(pom_cancel_btn, "translationX", 0f, -dp2px(45).toFloat())
        var alpha = ObjectAnimator.ofFloat(pom_cancel_btn, "alpha", 0f, 1f)

        trans.setDuration(ANIM_DURATION)
        trans1.setDuration(ANIM_DURATION)
        alpha.setDuration(ANIM_DURATION)

        animSet.setDuration(ANIM_DURATION)
        animSet.play(trans).with(trans1).with(alpha)
        animSet.start()
    }

    private fun pomStartAnim() {
        if (!mPomodoroService!!.getIsStart()) {
            pom_control_btn.setImageResource(R.drawable.pom_start_ic)
        } else {
            pom_control_btn.setImageResource(R.drawable.pom_pause_ic)
        }

        if (pom_cancel_btn.visibility != View.GONE) {
            var animSet = AnimatorSet()

            val trans = ObjectAnimator.ofFloat(pom_control_btn, "translationX", dp2px(40).toFloat(), 0f)
            val trans1 = ObjectAnimator.ofFloat(pom_cancel_btn, "translationX", -dp2px(40).toFloat(), 0f)
            var alpha = ObjectAnimator.ofFloat(pom_cancel_btn, "alpha", 1f, 0f)

            trans.setDuration(ANIM_DURATION)
            trans1.setDuration(ANIM_DURATION)
            alpha.setDuration(ANIM_DURATION)

            animSet.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    DisplayUtil.setViewVisibility(pom_cancel_btn, View.GONE)
                }
            })
            animSet.setDuration(ANIM_DURATION)
            animSet.play(trans).with(trans1).with(alpha)
            animSet.start()
        }
    }


    override fun onResume() {
        super.onResume()

    }

    fun bindService() {
        var mPomIntent = Intent(this, PomodoroService::class.java)

        //退出程序后依然能播放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(mPomIntent)
        } else {
            startService(mPomIntent)
        }

        bindService(mPomIntent, mServideConnect, Context.BIND_AUTO_CREATE)
    }

    inner class PomReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action.equals("com.dqc.puppymoney")) {
                val data = intent?.getSerializableExtra("pomdata") as PomSendData
                pom_view.setTime(data.mCurMills / 1000 / 60, data.mCurMills / 1000 % 60)
                pom_time.setText("${DisplayUtil.numberAddZero(data.mCurMills / 1000 / 60)}:${DisplayUtil.numberAddZero(data.mCurMills / 1000 % 60)}")
                Log.d("onReceiver", " " + data.mCurMills + " " + data.mCurCountDownStatus + " " + data.mMaxMinute + " " + data.mCurMode)
            }
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.open_wish_photo_in, R.anim.open_wish_photo_out)
    }

    override fun onDestroy() {
        super.onDestroy()

        //将播放的服务提升至前台服务
        val playIntent = Intent(this, PomodoroService::class.java)
        //Android 8.0以上
        //Android 8.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(playIntent)
        } else {
            startService(playIntent)
        }
    }

}