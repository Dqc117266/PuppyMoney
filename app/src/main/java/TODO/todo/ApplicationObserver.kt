package com.dqc.todo

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Lifecycle

import androidx.lifecycle.OnLifecycleEvent
import com.tencent.mmkv.MMKV


class ApplicationObserver: LifecycleObserver {
    private val TAG = this.javaClass.name
    private var mKV: MMKV? = null

    /**
     * ON_CREATE 在应用程序的整个生命周期中只会被调用一次
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.d(TAG, "Lifecycle.Event.ON_CREATE")

        mKV = MMKV.defaultMMKV()
        mKV?.encode("is_start_test", false)
    }

    /**
     * 应用程序出现到前台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.d(TAG, "Lifecycle.Event.ON_START")
    }

    /**
     * 应用程序出现到前台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Log.d(TAG, "Lifecycle.Event.ON_RESUME")
    }

    /**
     * 应用程序退出到后台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Log.d(TAG, "Lifecycle.Event.ON_PAUSE")
    }

    /**
     * 应用程序退出到后台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        Log.d(TAG, "Lifecycle.Event.ON_STOP")
    }

    /**
     * 永远不会被调用到，系统不会分发调用ON_DESTROY事件
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Log.d(TAG, "Lifecycle.Event.ON_DESTROY")
    }

}