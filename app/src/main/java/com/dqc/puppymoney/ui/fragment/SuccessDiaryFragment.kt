package com.dqc.puppymoney.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.PomSendData
import com.dqc.puppymoney.service.PomodoroService
import com.dqc.puppymoney.ui.activity.AddPomodoroActivity
import com.dqc.puppymoney.ui.activity.PomodoroActivity
import com.dqc.puppymoney.ui.view.CustomFontTextView
import com.dqc.puppymoney.ui.view.WaterWaveView3
import com.dqc.puppymoney.ui.view.WaterWaveView4
import com.dqc.puppymoney.ui.view.WaveView
import com.dqc.puppymoney.util.DisplayUtil
import com.dqc.puppymoney.util.dp2px
import kotlinx.android.synthetic.main.activity_pomodoro.*


class SuccessDiaryFragment : Fragment() {

    private val ANIM_DURATION = 400L
    private var mIntentActivityCode = 0
    private var mPomodoroService: PomodoroService? = null
    private var mLastPomSendData: PomSendData? =null
    private var mPomItem: RelativeLayout? = null
    private var mPomTime: CustomFontTextView? = null
    private var mPomControl: ImageButton? = null
    private var mPomCancel: ImageButton? = null
    private var mCancelIv: ImageView? = null

    private var mWaterWaveView3: WaterWaveView4? = null
    private var mWaveView1: WaveView? =null
    private var mWaveView2: WaveView? =null
    private var mWaveView3: WaveView? =null
    private var mPomWorkItemLayout: View? = null
    private var mPomStatusTv: CustomFontTextView? = null


    companion object {
        val INTENT_POM_CREATE_ACTIVITY_CODE = 0
        val INTENT_POM_ACTIVITY_CODE = 1
    }

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

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_success_diary, container, false)
        initView(view)
        registerBroadcast()
//        bindService()
        return view
    }

    fun bindService() {
        var mPomIntent = Intent(activity, PomodoroService::class.java)

        //退出程序后依然能播放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(mPomIntent)
        } else {
            activity?.startService(mPomIntent)
        }

        activity?.bindService(mPomIntent, mServideConnect, Context.BIND_AUTO_CREATE)
    }

    private fun onServiceConnect() {
        refreshIntentActivityCode()

        if (mPomodoroService!!.getIsStart()) {
            addPomWorkView()
            mPomControl?.setImageResource(R.drawable.pom_pause_ic)

            mWaterWaveView3?.pauseWaterWaveAnim()
            mWaveView1?.stopWava()
            mWaveView2?.stopWava()
            mWaveView3?.stopWava()
        } else {
            if (mPomodoroService!!.getIsCancel())
                return

            addPomWorkView()
            pomPauseAnim()

            mWaterWaveView3?.startWaterWaveAnim()
            mWaveView1?.startWava()
            mWaveView2?.startWava()
            mWaveView3?.startWava()
        }

        val countDownMills = mPomodoroService?.getCountDownMills()
        mPomTime?.setText("${DisplayUtil.numberAddZero(DisplayUtil.millsToMinute(countDownMills!!.toInt()))}:${DisplayUtil.numberAddZero(DisplayUtil.millsToSecond(countDownMills!!.toInt()))}")

    }

    private fun registerBroadcast() {
        var pomReceivr = PomReceiver()
        var intentFilter = IntentFilter()
        intentFilter.addAction("com.dqc.puppymoney")
        activity?.registerReceiver(pomReceivr, intentFilter)
    }

    private fun initView(view: View?) {
        Log.d("SuccessDiary", " initView ")
        var tv: CustomFontTextView = view?.findViewById(R.id.success_diary_count)!!
        mPomItem = view.findViewById(R.id.add_pomodoro_item)

        val spannableString = SpannableString("12/5")
        spannableString.setSpan(AbsoluteSizeSpan(context!!.resources.getDimension(R.dimen.success_diary_count_tv_size).toInt()), 0, "12/5".indexOf('/'), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        tv.setText(spannableString)

        mPomItem?.setOnClickListener {
            if (mIntentActivityCode == INTENT_POM_ACTIVITY_CODE) {
                var intent = Intent(activity, PomodoroActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.open_wish_photo_in, R.anim.open_wish_photo_out)
            } else if (mIntentActivityCode == INTENT_POM_CREATE_ACTIVITY_CODE) {
                var intent = Intent(activity, AddPomodoroActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.open_wish_photo_in, R.anim.open_wish_photo_out)
            }
        }
    }

    private fun refreshIntentActivityCode() {
        Log.d("ReFRESH", " ${mPomodoroService!!.mPomSenDate != null} ${mPomodoroService!!.mPomSenDate?.mCurMode}")
        if (mPomodoroService!!.mPomSenDate != null
                && mPomodoroService!!.mPomSenDate?.mCurMode == PomSendData.POM_OVER_STATUS) {
            mIntentActivityCode = INTENT_POM_CREATE_ACTIVITY_CODE
        } else if (mPomodoroService!!.mPomSenDate != null && (mPomodoroService!!.mPomSenDate?.mCurMode == PomSendData.POM_WORK_MODE
                || mPomodoroService!!.mPomSenDate?.mCurMode == PomSendData.POM_REST_MODE)) {
            mIntentActivityCode = INTENT_POM_ACTIVITY_CODE
            Log.d("ReFRESH", " mIntentActivityCode INTENT_POM_ACTIVITY_CODE " + mIntentActivityCode)
        }

    }

    private fun pomStartAnim() {
        if (!mPomodoroService!!.getIsStart()) {
            mPomControl?.setImageResource(R.drawable.pom_little_start_ic)
        } else {
            mPomControl?.setImageResource(R.drawable.pom_little_pause_ic)
        }

        if (mPomCancel?.visibility != View.GONE) {
            var animSet = AnimatorSet()

            val trans = ObjectAnimator.ofFloat(mPomControl!!, "translationX", activity?.dp2px(32)!!.toFloat(), 0f)
            val trans1 = ObjectAnimator.ofFloat(mPomCancel!!, "translationX", -activity?.dp2px(32)!!.toFloat(), 0f)
            var alpha = ObjectAnimator.ofFloat(mPomCancel!!, "alpha", 1f, 0f)

            trans.setDuration(ANIM_DURATION)
            trans1.setDuration(ANIM_DURATION)
            alpha.setDuration(ANIM_DURATION)

            animSet.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    DisplayUtil.setViewVisibility(mPomCancel!!, View.GONE)
                }
            })
            animSet.setDuration(ANIM_DURATION)
            animSet.play(trans).with(trans1).with(alpha)
            animSet.start()
        }
    }

    private fun pomPauseAnim() {
        DisplayUtil.setViewVisibility(mPomCancel!!, View.VISIBLE)
        if (!mPomodoroService!!.getIsStart()) {
            mPomControl?.setImageResource(R.drawable.pom_little_start_ic)
        } else {
            mPomControl?.setImageResource(R.drawable.pom_little_pause_ic)
        }

        var animSet = AnimatorSet()

        var trans = ObjectAnimator.ofFloat(mPomControl!!, "translationX", 0f, activity?.dp2px(32)!!.toFloat())
        var trans1 = ObjectAnimator.ofFloat(mPomCancel!!, "translationX", 0f, -activity?.dp2px(32)!!.toFloat())
        var alpha = ObjectAnimator.ofFloat(mPomCancel!!, "alpha", 0f, 1f)

        trans.setDuration(ANIM_DURATION)
        trans1.setDuration(ANIM_DURATION)
        alpha.setDuration(ANIM_DURATION)

        animSet.setDuration(ANIM_DURATION)
        animSet.play(trans).with(trans1).with(alpha)
        animSet.start()
    }

    inner class PomReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action.equals("com.dqc.puppymoney")) {
                val data = intent?.getSerializableExtra("pomdata") as PomSendData
                Log.d("Services", " PomReceiver mode " + data.mCurMode)
                if (mLastPomSendData == null) {
                    refershPomMode(data)
                    Log.d("Services", "mLastPomSendData == null PomReceiver mode " + data.mCurMode)
                }

                if (mLastPomSendData != null && mLastPomSendData?.mCurMode != data.mCurMode) {
                    refershPomMode(data)
                    Log.d("LastpomSendData", "mCurMode ${mLastPomSendData?.mCurMode}")
                    Log.d("Services", "mLastPomSendData != null && mLastPomSendData?.mCurMode != data.mCurMode PomReceiver mode " + data.mCurMode)
//                    refreshPomStatus(data)
                }

                if (mLastPomSendData != null && mLastPomSendData?.mCurCountDownStatus != data.mCurCountDownStatus) {
                    refreshPomStatus(data)
                    Log.d("LastpomSendData", "mCurCountDownStatus ${data.mCurCountDownStatus}")
                    Log.d("Services", "mLastPomSendData != null && mLastPomSendData?.mCurCountDownStatus != data.mCurCountDownStatus PomReceiver mode " + data.mCurMode)

                }

                refshPomTime(data)
                mLastPomSendData = data
                Log.d("SuccessPomRec", " " + data.mCurMode + " " + data.mCurMills + " " + data.mCurCountDownStatus)
            }
        }
    }

    private fun refshPomTime(pomSendData: PomSendData) {
        when (pomSendData.mCurMode) {
            PomSendData.POM_WORK_MODE -> {
                var mills = pomSendData.mCurMills
                mPomTime?.setText("${DisplayUtil.numberAddZero(DisplayUtil.millsToMinute(mills))}:${DisplayUtil.numberAddZero(DisplayUtil.millsToSecond(mills))}")
            }

            PomSendData.POM_REST_MODE -> {
                var mills = pomSendData.mCurMills
                mPomTime?.setText("${DisplayUtil.numberAddZero(DisplayUtil.millsToMinute(mills))}:${DisplayUtil.numberAddZero(DisplayUtil.millsToSecond(mills))}")
                mWaterWaveView3?.setCurMills(mills)
            }
        }
    }

    private fun refershPomMode(pomSendData: PomSendData) {
        Log.d("DQCDQC"," pomSendData " + pomSendData.mCurMode)
        when (pomSendData.mCurMode) {
            PomSendData.POM_WORK_MODE -> {
                mIntentActivityCode = INTENT_POM_ACTIVITY_CODE
                addPomWorkView()
//                mPomStatusTv?.setText(R.string.wish_success_diary_pom_mode_work)
            }

            PomSendData.POM_REST_MODE -> {
                mIntentActivityCode = INTENT_POM_ACTIVITY_CODE

                if (mPomWorkItemLayout != null) {
                    mPomTime?.setText("${mPomodoroService?.mCurMinute!!}:00")
                    mWaterWaveView3?.setMinute(mPomodoroService?.mCurMinute!!)
                    mPomStatusTv?.setText(R.string.wish_success_diary_pom_mode_rest)

                    pomStartAnim()
                    mWaterWaveView3?.pauseWaterWaveAnim()
                    mWaveView1?.stopWava()
                    mWaveView2?.stopWava()
                    mWaveView3?.stopWava()
                }
            }

            PomSendData.POM_OVER_STATUS -> {
                mIntentActivityCode = INTENT_POM_CREATE_ACTIVITY_CODE

//                if (mPomWorkItemLayout != null) {
//                    mPomTime?.setText("${mPomodoroService?.mCurMinute!!}:00")
//                    mWaterWaveView3?.setMinute(mPomodoroService?.mCurMinute!!)
//
//                    pomStartAnim()
//                    mWaterWaveView3?.pauseWaterWaveAnim()
//                    mWaveView1?.stopWava()
//                    mWaveView2?.stopWava()
//                    mWaveView3?.stopWava()
//                }
                addPomNormollView()
                mLastPomSendData = null
            }
        }
    }

    private fun refreshPomStatus(pomSendData: PomSendData) {
        when (pomSendData.mCurCountDownStatus) {
            PomSendData.POM_START_STATUS -> {
                pomStartAnim()
//                mPomControl?.setImageResource(R.drawable.pom_little_pause_ic)
                mWaterWaveView3?.startWaterWaveAnim()
                mWaveView1?.startWava()
                mWaveView2?.startWava()
                mWaveView3?.startWava()
            }

            PomSendData.POM_PAUSE_STATUS -> {
                pomPauseAnim()
                mWaterWaveView3?.pauseWaterWaveAnim()
                mWaveView1?.stopWava()
                mWaveView2?.stopWava()
                mWaveView3?.stopWava()
            }

            PomSendData.POM_CANCEL_STATUS -> {

            }
        }
    }

    private fun addPomNormollView() {
        mPomItem?.removeAllViews()
        var view = LayoutInflater.from(context).inflate(R.layout.pom_normal_item_layout, null)
        mPomItem?.addView(view)
    }

    private fun addPomWorkView() {
        mPomItem?.removeAllViews()
        mPomWorkItemLayout = LayoutInflater.from(context).inflate(R.layout.pom_work_item_layout, null)
        mPomItem?.addView(mPomWorkItemLayout)
        mPomTime = mPomItem?.findViewById(R.id.fragment_pom_time)!!
        mPomStatusTv = mPomItem?.findViewById(R.id.fragment_pom_status_tv)!!

        mWaterWaveView3 = mPomItem?.findViewById(R.id.water_wave_view)
        mWaterWaveView3?.setMinute(mPomodoroService?.mMinute!!)

        mWaveView1 = mPomItem?.findViewById(R.id.wave_1)
        mWaveView2 = mPomItem?.findViewById(R.id.wave_2)
        mWaveView3 = mPomItem?.findViewById(R.id.wave_3)


        mPomControl = mPomItem?.findViewById(R.id.pom_control_btn)
        mPomCancel = mPomItem?.findViewById(R.id.pom_cancel_btn)
        mCancelIv = mPomItem?.findViewById(R.id.cancel_pom_iv)

        mCancelIv!!.setOnClickListener {

        }

        if (mPomodoroService!!.getIsStart()) {
            mWaterWaveView3?.startWaterWaveAnim()
        } else {
            mWaterWaveView3?.pauseWaterWaveAnim()
        }

        mPomControl?.setOnClickListener {
            if (mPomodoroService!!.getIsStart()) {
                mPomodoroService!!.pausePomodoro()
                pomPauseAnim()

                mWaterWaveView3?.pauseWaterWaveAnim()
                mWaveView1?.stopWava()
                mWaveView2?.stopWava()
                mWaveView3?.stopWava()
            } else {
                mPomodoroService!!.startPomodoro()
                mPomItem?.post {
                    pomStartAnim()
                }

                mWaterWaveView3?.startWaterWaveAnim()
                mWaveView1?.startWava()
                mWaveView2?.startWava()
                mWaveView3?.startWava()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mWaterWaveView3 != null) {
            mWaterWaveView3?.mWaterWaveAnimator?.cancel()
            mWaterWaveView3?.mWaterWaveAnimator = null
        }
    }
}