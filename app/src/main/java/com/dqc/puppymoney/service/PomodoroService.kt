package com.dqc.puppymoney.service

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dqc.puppymoney.R
import com.dqc.puppymoney.bean.PomSendData
import com.dqc.puppymoney.ui.activity.WishActivity
import com.tencent.mmkv.MMKV
import kotlin.math.min

class PomodoroService : Service() {

    private val HANDLER_WHAT = 100
    private val NOTIFICATION_ID = 98
    private var mMediaPlayer: MediaPlayer? = null
    private var mRestMinute = 5
    private var mPomCount = 4
    private var mCurMode = 0
    private var mCurPomCount = 0

    private var mCurrentMills = 0L
    private var mCountDownMills = 0L

    private var mPauseMills = 0L

    private var mIsStart = false
    private var mIsRuning = false
    private var mIsPause = false
    private var mIsCancel = true
    private var mKV: MMKV? = null

    var mCurMinute = 0
    var mMinute = 25
    var mPomSenDate: PomSendData? = null

    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            updateTime()

            if (mIsStart) {
                sendEmptyMessageDelayed(HANDLER_WHAT, 1000)
            }
        }
    }

    fun setPomParam(minute: Int, restMinute: Int, pomCount: Int) {
        if (mIsCancel) {
            mMinute = minute
            mCurMinute = mMinute
            mRestMinute = restMinute
            mPomCount = pomCount
        }
    }

    private fun updateTime() {
        var allMills = mCurMinute * 1 * 1000
        var curMills = SystemClock.elapsedRealtime() - mCurrentMills
        mCountDownMills = allMills - curMills
        Log.d("Services", " # m = ${mCountDownMills / 1000 / 60} s = ${mCountDownMills / 1000 % 60}")
        if (mCountDownMills > 0) {
            sendPomBroadcast()
        } else {
            changePomMode()
            resetPomodoro()
        }
    }

    fun restTimeOk() {
        if (mCurMode == 1) {
            cancelPomodoro()
            changePomMode()
        }
    }

    fun changePomMode() {
        Log.d("Services", " changePomMode mCurMode = " + mCurMode)
        if (mCurMode == 0) {
            mCurMode = 1
            mCurMinute = mRestMinute
            mCurPomCount ++
            Log.d("Services", " changePomMode mCurMinute = " + mCurMode)
        } else {
            if (mCurPomCount == mPomCount) {
                mCurMode = 2
                sendPomBroadcast()
                return
            }
            mCurMode = 0
            mCurMinute = mMinute
        }
    }

    fun sendPomBroadcast() {
        var intent = Intent()
        intent.setAction("com.dqc.puppymoney")

        mPomSenDate = PomSendData()
        var cdStatus: Int = -1
        if (mIsStart) {
            cdStatus = PomSendData.POM_START_STATUS
        } else if (mIsPause) {
            cdStatus = PomSendData.POM_PAUSE_STATUS
        } else if (mIsCancel) {
            cdStatus = PomSendData.POM_CANCEL_STATUS
        }

        var mode = -1
        if (mCurMode == 0) {
            mode = PomSendData.POM_WORK_MODE
        } else if (mCurMode == 1) {
            mode = PomSendData.POM_REST_MODE
        } else if (mCurMode == 2) {
            mode = PomSendData.POM_OVER_STATUS
        }
        mPomSenDate?.mCurCountDownStatus = cdStatus

        mPomSenDate?.mCurMode = mode
        mPomSenDate?.mMaxMinute = mCurMinute
        mPomSenDate?.mCurMills = mCountDownMills.toInt()

        intent.putExtra("pomdata", mPomSenDate)

        Log.d("Services", " mode " + mode + " mCurMode " + mCurMode)
        sendBroadcast(intent)
    }

    fun startPomodoro() {
        mIsStart = true
        mIsCancel = false
        if (!mIsRuning) {
            mIsRuning = true
            mCurrentMills = SystemClock.elapsedRealtime()
            mHandler.sendEmptyMessageDelayed(HANDLER_WHAT, 0)
        }

        if (mIsPause) {
            mIsPause = false
            mCurrentMills += (SystemClock.elapsedRealtime() - mPauseMills)
            mHandler.sendEmptyMessageDelayed(HANDLER_WHAT, 0)
        }

//        mCurMode = 0
    }

    fun pausePomodoro() {
        if (!mIsPause && !mIsCancel) {
            mIsPause = true
            mIsStart = false
            mPauseMills = SystemClock.elapsedRealtime()
            mHandler.removeMessages(HANDLER_WHAT)
            sendPomBroadcast()
        }
    }

    fun resetPomodoro() {
        if (mIsRuning) {
            mIsCancel = true
            mIsRuning = false
            mIsPause = false
            mIsStart = false
            mHandler.removeMessages(HANDLER_WHAT)
            mCountDownMills = mCurMinute * 1000 * 60L
            sendPomBroadcast()
        }
    }

    fun cancelPomodoro() {
        if (mIsRuning) {
            mIsCancel = true
            mIsRuning = false
            mIsPause = false
            mIsStart = false
            mHandler.removeMessages(HANDLER_WHAT)
            mCountDownMills = mCurMinute * 1000 * 60L

            mCurMode = 2
            sendPomBroadcast()
        }
    }

    fun getIsStart(): Boolean {
        return mIsStart
    }

    fun getIsPause(): Boolean {
        return mIsPause
    }

    fun getIsCancel(): Boolean {
        return mIsCancel
    }

    fun getCountDownMills(): Long {
        return mCountDownMills
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        mKV = MMKV.defaultMMKV()
//        mPomCount = mKV?.decodeInt("pom_count")!!
//        mMinute = mKV?.decodeInt("pom_dartion")!!
//        mRestMinute = mKV?.decodeInt("pom_rest_dartion")!!

        mCurMode = 0
        mCurMinute = mMinute

        mMediaPlayer = MediaPlayer.create(this, R.raw.silent)
        mMediaPlayer?.isLooping = false

        startForeground(NOTIFICATION_ID, getNotification("Pomodoro"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread({
            startPlaySong()
        }).start()
        return START_STICKY
    }

    private fun startPlaySong() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.silent)
            mMediaPlayer?.start()
        } else {
            mMediaPlayer?.start()
        }
        Log.d("PomdoroService", " mMediaPlayer.isPlaying " + mMediaPlayer?.isPlaying)

        mMediaPlayer?.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
            override fun onCompletion(mp: MediaPlayer?) {
//                mMediaPlayer?.prepare()
                mMediaPlayer?.start()
                Log.d("PomdoroService", "mMediaPlayer.start" )

            }
        })
//        try {
//            Thread.sleep(3000)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//        Log.d("PomdoroService", "3000ms sleep after" )

//        if (mMediaPlayer != null) {
//            mMediaPlayer?.pause()
//        }
        Log.d("PomdoroService", " pause " )

    }

    private fun stopPlaySong() {
        if (mMediaPlayer != null) {
            mMediaPlayer?.stop()
            mMediaPlayer?.release()
            mMediaPlayer = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return PomodoroBinder()
    }

    inner class PomodoroBinder: Binder() {
        val service: PomodoroService
        get() = this@PomodoroService
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    //开启前台服务
    private fun getNotificationManager(): NotificationManager? {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onDestroy() {
        super.onDestroy()
//        mMediaPlayer?.pause()
//        stopPlaySong()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(applicationContext, PomodoroService::class.java))
        } else {
            startService(Intent(applicationContext, PomodoroService::class.java))
        }
//        stopForeground(true)
    }

    //设置通知栏标题
    private fun getNotification(title: String): Notification? {
        val intent = Intent(this, WishActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = "play"
            val name = "番茄钟"
            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW)
            getNotificationManager()?.createNotificationChannel(mChannel)
            val builder = Notification.Builder(this, id)
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setContentIntent(pi)
            builder.setContentTitle(title)
            builder.build()
        } else {
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "play")
            builder.setSmallIcon(R.mipmap.ic_launcher_round)
            builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
            builder.setContentIntent(pi)
            builder.setContentTitle(title)
            builder.build()
        }
    }
}