package com.dqc.todo

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.tencent.mmkv.MMKV

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationObserver())
    }
}