package com.dqc.puppymoney.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.OrientationHelper
import com.dqc.puppymoney.R
import com.dqc.puppymoney.ui.adapter.OrsoPageAdapter
import com.dqc.puppymoney.ui.recyclerview.OnViewPagerListener
import com.dqc.puppymoney.ui.recyclerview.OrsoPageLayoutManager
import com.dqc.puppymoney.ui.view.DotViews
import com.dqc.puppymoney.util.DisplayUtil
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_wish_to_enter.*

class WishToEnterActivity :  AppCompatActivity() {

    private lateinit var orsoPageLayoutManager: OrsoPageLayoutManager;
    private lateinit var immersionBar: ImmersionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_to_enter)
        immersionBar = ImmersionBar.with(this)
        immersionBar.statusBarDarkFont(true)
        immersionBar.init()

        orsoPageLayoutManager = OrsoPageLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        wish_to_enter_rv.layoutManager = orsoPageLayoutManager
        wish_to_enter_rv.adapter = OrsoPageAdapter()
        dot_views.setRecyclerView(wish_to_enter_rv)
        wish_to_enter_complete_btn.isEnabled = false

        orsoPageLayoutManager.setViewPageListener(object : OnViewPagerListener {
            override fun onPageRelease(isNest: Boolean, view: View) {

            }

            override fun onPagePosition(position: Int) {
                Log.d("CCC", " position " + position)
            }

            override fun onPageSelected(select: Boolean, view: View?, position: Int) {
                var editText = view as EditText
                editText.isFocusableInTouchMode = true
                editText.isFocusable = true
                editText.requestFocus()

                dot_views.setSelectPosition(editText, position)
                Log.d("CCC", "onPageSelected " + editText.text.toString())
            }
        })

        dot_views.setOnAllInputCompleteListener(object : DotViews.OnAllInputCompleteListener {
            override fun onIsAllInputComplete(isAllInputComplete: Boolean) {
                if (isAllInputComplete) {
                    wish_to_enter_complete_btn.isEnabled = true
                } else {
                    wish_to_enter_complete_btn.isEnabled = false
                }
            }

        })

        wish_to_enter_complete_btn.setOnClickListener {
            var isRepeat = false
            val allInputText = dot_views.getAllInputText()
            var stringBuffer = StringBuffer()
            var repeatSb = StringBuffer()
            var index = 0
            for (str in allInputText) {
                if (stringBuffer.indexOf("," + str + ",") > -1) {
                    isRepeat = true
                    repeatSb.append(DisplayUtil.numToChinese(index + 1)).append("，")
//                        Log.d("AAAAAA", "$index")
                } else {
                    stringBuffer.append(",").append(str).append(",")
                }
                index++
            }

            if (!isRepeat) {
                val intent = Intent(this, WishToChooseActivity::class.java)
                intent.putExtra("wish_list", dot_views.getAllInputText())
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.wish_to_enter_repeat_tip, repeatSb.toString()), Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar.destroy()
    }
}