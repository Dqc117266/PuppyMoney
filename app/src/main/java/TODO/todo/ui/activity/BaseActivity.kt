package com.dqc.todo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.barlibrary.ImmersionBar

open class BaseActivity: AppCompatActivity() {

    lateinit var mImmersionBar: ImmersionBar
    override fun onCreate(savedInstanceState: Bundle?) {
        //深灰
        super.onCreate(savedInstanceState)
        mImmersionBar = ImmersionBar.with(this)
            .statusBarDarkFont(false)
        mImmersionBar.init()
    }

    override fun onDestroy() {
        super.onDestroy()
        mImmersionBar.destroy()
    }
}