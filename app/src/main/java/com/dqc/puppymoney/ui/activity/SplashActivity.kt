package com.dqc.puppymoney.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dqc.puppymoney.R
import com.tencent.mmkv.MMKV

class SplashActivity : AppCompatActivity() {
    private var kv: MMKV? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        kv = MMKV.defaultMMKV()
        val isFirstStart: Boolean = kv?.decodeBool("is_first_start")!!
        if (!isFirstStart) {
            var intent = Intent(this, WishToEnterActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            var intent = Intent(this, WishActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}