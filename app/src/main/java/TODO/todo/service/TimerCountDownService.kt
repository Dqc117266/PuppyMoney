package com.dqc.todo.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.dqc.todo.utils.allSecond2HourAndMinute
import com.tencent.mmkv.MMKV
import java.util.*

class TimerCountDownService: Service() {

    private var mSeconds: Int = 0
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private var mKV: MMKV? = null
    private var mIsStarted: Boolean = false

    override fun onCreate() {
        super.onCreate()
        mKV = MMKV.defaultMMKV()
        var minute = mKV?.decodeInt("todo_task_minute", 0)!!
        var money = mKV?.decodeDouble("todo_task_money", 0.0)!!

        setMinutes(minute, money)
    }

    fun setMinutes(minute: Int, money: Double) {
        mSeconds = minute * 60
        if (mTimer == null
            && mTimerTask == null) {
            mTimer = Timer()
            mTimerTask = object :TimerTask() {
                override fun run() {
                    mSeconds --

                    var timeStr = this@TimerCountDownService.allSecond2HourAndMinute(mSeconds)
                    val intent = Intent()
                    intent.putExtra("timer", timeStr)
                    intent.setAction("com.dqc.timer")

                    if (mSeconds == 0) {
                        cancelTask()
                        val defaultMMKV = MMKV.defaultMMKV()
                        val decodeInt = defaultMMKV.decodeDouble("all_money_size")
                        defaultMMKV.encode("all_money_size", decodeInt + money)

                        intent.putExtra("task_ok", "ok")
                        intent.setAction("com.dqc.timer")
                    }

                    sendBroadcast(intent)
                }
            }
            mTimer?.schedule(mTimerTask, 0, 1000)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTask()
    }

    private fun cancelTask() {
        if (mTimer != null) {
            mTimer?.cancel()
            mTimer = null
        }

        if (mTimerTask != null) {
            mTimerTask?.cancel()
            mTimerTask = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {

        return TimerCountDownBinder()
    }

    inner class TimerCountDownBinder: Binder() {
        val service: TimerCountDownService
        get() = this@TimerCountDownService
    }
}