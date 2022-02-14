package com.dqc.todo.service

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.dqc.todo.R
import com.dqc.todo.database.bean.TodoListBean
import com.dqc.todo.ui.activity.TodoTaskActivity
import com.dqc.todo.utils.allSecond2HourAndMinute
import com.tencent.mmkv.MMKV
import java.util.*

class TimerCountDownService1: Service() {

    private val NOTIFICATION_ID = 98
    private var mSeconds: Int = 0
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private var mKV: MMKV? = null
    var mTodoListBean: TodoListBean? = null
    var mTimerStr: String? = null
    var mIsStarted: Boolean = false


    override fun onCreate() {
        super.onCreate()
        mKV = MMKV.defaultMMKV()
        startForeground(NOTIFICATION_ID, getNotification("TODO"))
    }

    fun setMinutes(minute: Int, money: Double) {
        mSeconds = minute * 60
        if (mTimer == null
            && mTimerTask == null) {
            mTimer = Timer()
            mTimerTask = object :TimerTask() {
                override fun run() {
                    mSeconds --

                    mTimerStr = this@TimerCountDownService1.allSecond2HourAndMinute(mSeconds)
                    val intent = Intent()
                    intent.putExtra("timer", mTimerStr)
                    intent.setAction("com.dqc.timer")

                    if (mSeconds == 0) {
                        cancelTimerCountDown()
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

    fun setTodoBean(todoBean: TodoListBean) {
        mTodoListBean = todoBean
    }

    fun startTimerCountDown() {
        if (mTodoListBean != null) {
            setMinutes(mTodoListBean!!.todoDuration.toInt(),
                mTodoListBean!!.todoRewardAmount.toDouble())
            mIsStarted = true
        }
    }

    fun cancelTimerCountDown() {
        cancelTask()
        mIsStarted = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTask()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
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
        val service: TimerCountDownService1
        get() = this@TimerCountDownService1
    }


    //开启前台服务
    private fun getNotificationManager(): NotificationManager? {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    //设置通知栏标题
    private fun getNotification(title: String): Notification? {
        val intent = Intent(this, TodoTaskActivity::class.java)
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