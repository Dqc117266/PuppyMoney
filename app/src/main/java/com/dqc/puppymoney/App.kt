package com.dqc.puppymoney

import android.app.Application
import com.dqc.puppymoney.database.AppDatabase
import com.tencent.mmkv.MMKV

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        AppDatabase.getInstance(this)
        clearFile()
    }

    fun clearFile() {

    }

}