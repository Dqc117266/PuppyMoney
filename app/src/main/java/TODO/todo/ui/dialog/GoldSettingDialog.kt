package com.dqc.todo.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import com.dqc.todo.R
import kotlinx.android.synthetic.main.dialog_gold_setting_layout.*

class GoldSettingDialog(context: Activity) : Dialog(context) {
    private var mICallBack: IClickConfirmCallBack ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_gold_setting_layout)

        initView()
    }

    fun setConfirmCallBack(iCallBack: IClickConfirmCallBack) {
        mICallBack = iCallBack
    }

    private fun initView() {

        edit_gold.setInputType(InputType.TYPE_CLASS_NUMBER)

        gold_setting_cancel.setOnClickListener {
            val mInputMethodManager: InputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), 0)
            dismiss()
        }

        gold_setting_confirm.setOnClickListener {
            val mInputMethodManager: InputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), 0)

            if (mICallBack != null) {
                mICallBack?.onClickConfirm(edit_gold.text.toString())
            }
            dismiss()
        }

    }

    interface IClickConfirmCallBack {
        fun onClickConfirm(goldSize: String);
    }
}