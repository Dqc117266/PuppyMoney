package com.dqc.todo.ui.activity

import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.dqc.todo.R
import com.dqc.todo.database.bean.TodoListBean
import com.dqc.todo.service.TimerCountDownService
import com.dqc.todo.service.TimerCountDownService1
import com.dqc.todo.utils.minute2HourAndMinute
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_todo_task.*

class TodoTaskActivity : BaseActivity() {

    private var mMinute: Int = 0
    private var mKV: MMKV ?= null
    private var mIsStartTest: Boolean? = null
    private var mTimerReceiver: TimerReceive? = null
    private var mTodoTitle: String? = null
    private var mAllMoney: String? = null
    private var mTimerCountDownService: TimerCountDownService1? = null
    private var mTodoBean: TodoListBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_task)
        mKV = MMKV.defaultMMKV()

        initView()
        bindService()
        registerBroadcast()
    }

    private var mServideConnect: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var binder = service as TimerCountDownService1.TimerCountDownBinder
            mTimerCountDownService = binder.service
            onServiceConnect()
            Log.d("BindService", " onServiceConnected ")
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    fun onServiceConnect() {
        if (mTimerCountDownService != null) {
            if (mTimerCountDownService?.mIsStarted!!) {
                count_down_tv.setText(mTimerCountDownService?.mTimerStr)
            }
            Log.d("BindService", " ${mTimerCountDownService != null}  ${mTimerCountDownService?.mIsStarted!!}")

            if (mTodoBean != null
                && mTimerCountDownService?.mTodoListBean != null) {

                if (mTimerCountDownService?.mIsStarted!!
                    && mTodoBean?.tid != mTimerCountDownService?.mTodoListBean?.tid) {
                    control_btn.setText(getString(R.string.task_wait_other))
                    control_btn.isEnabled = false
                    return
                }
            }

            if (mTimerCountDownService?.mIsStarted!!) {
                textUpdata(true)
            } else {
                textUpdata(false)
            }
        }

    }

    fun bindService() {
        var intent = Intent(this, TimerCountDownService1::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }

        bindService(intent, mServideConnect, Context.BIND_AUTO_CREATE)
    }

    private fun initView() {
        mTodoBean = intent.getParcelableExtra<TodoListBean>("todo_bean")
        todo_task_title.setText(mTodoBean?.todoTitle)
        mMinute = mTodoBean?.todoDuration!!.toInt()
        mTodoTitle = mTodoBean?.todoTitle
        mAllMoney = mTodoBean?.todoRewardAmount

        gold_size_tv.setText(mTodoBean?.todoRewardAmount)
        count_down_tv.setText(minute2HourAndMinute(mMinute))

        close_page_iv.setOnClickListener {
            onBackPressed()
        }

        control_btn.setOnClickListener {
            if (mTimerCountDownService != null
                && !mTimerCountDownService?.mIsStarted!!) {

                mTimerCountDownService?.setTodoBean(mTodoBean!!)
                mTimerCountDownService?.startTimerCountDown()
                textUpdata(true)
            } else {
                mTimerCountDownService?.cancelTimerCountDown()
                textUpdata(false)
                count_down_tv.setText(minute2HourAndMinute(mMinute))
            }

            Log.d("TAGTAG", " ${mTimerCountDownService != null} ")
        }
    }

    private fun textUpdata(isStart: Boolean) {
        if (isStart) {
            control_btn.setText(getString(R.string.task_cancel_text))
        } else {
            control_btn.setText(getString(R.string.task_start_text))
        }
    }

    private fun registerBroadcast() {
        mTimerReceiver = TimerReceive()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.dqc.timer")
        registerReceiver(mTimerReceiver, intentFilter)
    }

    private fun startTimerService() {
        val intent = Intent(this, TimerCountDownService::class.java)
        mKV?.encode("todo_task_minute", mMinute)
        mKV?.encode("todo_task_money", mAllMoney!!.toDouble())
        startService(intent)

        mKV?.encode("is_start_test", true)
        mKV?.encode("start_task_title", mTodoTitle)

        mIsStartTest = true
        textUpdata(true)

    }

    private fun stopTimerService() {
        val intent = Intent(this, TimerCountDownService::class.java)
        stopService(intent)

        mKV?.encode("is_start_test", false)
        mIsStartTest = false
        textUpdata(false)

    }

    private fun stopReciver() {
        if (mTimerReceiver != null) {
            unregisterReceiver(mTimerReceiver)
            mTimerReceiver = null
        }
    }

    inner class TimerReceive: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action.equals("com.dqc.timer")) {
                val stringExtra = intent?.getStringExtra("timer")
                count_down_tv.setText(stringExtra)

                if (mTimerCountDownService?.mIsStarted!!
                    && mTodoBean?.tid != mTimerCountDownService?.mTodoListBean?.tid) {
                }

                if (intent!!.hasExtra("task_ok")) {
                    resetView()
                }
            }
        }
    }

    private fun resetView() {
        if (!control_btn.isEnabled) {
            control_btn.isEnabled = true
        }
        control_btn.setText(getString(R.string.task_start_text))
        count_down_tv.setText(this.minute2HourAndMinute(mMinute))

    }
}