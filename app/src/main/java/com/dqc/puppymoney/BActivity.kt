package com.dqc.puppymoney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class BActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Activity", "BActivity onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.d("Activity", "BActivity onStart")

    }

    override fun onResume() {
        super.onResume()
        Log.d("Activity", "BActivity onResume")

    }

    override fun onPause() {
        super.onPause()
        Log.d("Activity", "BActivity onPause")

    }

    override fun onStop() {
        super.onStop()
        Log.d("Activity", "BActivity onStop")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Activity", "BActivity onDestroy")

    }


}