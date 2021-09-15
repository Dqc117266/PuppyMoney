package com.dqc.puppymoney.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.dqc.puppymoney.R
import com.dqc.puppymoney.interfaces.IPomCountCallBack
import com.dqc.puppymoney.interfaces.IPomDartionCallBack
import com.gyf.barlibrary.ImmersionBar
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_add_pomodoro.*

class AddPomodoroActivity : BaseActivity() {

    private lateinit var mPomTextArray: Array<String>
    private lateinit var mPomRestTextArray: Array<String>
    private var mPomCount = 1
    private var mPomDartionIndex = 3
    private var mPomRestDartionIndex = 3
    private var mKV: MMKV? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pomodoro)
        mImmersionBar = ImmersionBar.with(this)
                .statusBarDarkFont(true)
        mImmersionBar.init()

        mKV = MMKV.defaultMMKV()

        initView()
    }

    private fun initView() {
        mPomTextArray = resources.getStringArray(R.array.pom_text)
        mPomRestTextArray = resources.getStringArray(R.array.pom_rest_text)

        pom_count_bar.setIPomCountCallBack(object :IPomCountCallBack {
            override fun onPomCount(count: Int) {
                mPomCount = count + 1
                pom_count_tv.setText("×${mPomCount}")
            }
        })

        pom_dartion_bar.setIPomDartionCallBack(object :IPomDartionCallBack {
            override fun onDartion(dartion: Int) {
                mPomDartionIndex = dartion
                pom_dartion_tv.setText(mPomTextArray[dartion])
            }
        })

        pom_rest_dartion_bar.setIPomDartionCallBack(object :IPomDartionCallBack {
            override fun onDartion(dartion: Int) {
                mPomRestDartionIndex = dartion
                pom_rest_dartion_tv.setText(mPomRestTextArray[dartion])
            }
        })

        add_pomodoro_close_page.setOnClickListener {
            val mInputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), 0)
            onBackPressed()
        }

        pom_start_btn.setOnClickListener {
            mKV?.encode("pom_count", mPomCount)
            mKV?.encode("pom_dartion", 10 + mPomDartionIndex * 5)
            mKV?.encode("pom_rest_dartion", 2 + mPomRestDartionIndex)

            var intent = Intent(this, PomodoroActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(null != this.getCurrentFocus()) {
            val mInputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            return mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), 0)
        }
        return false
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.open_wish_photo_in, R.anim.open_wish_photo_out)
    }
}