package com.dqc.todo.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.dqc.todo.R
import kotlinx.android.synthetic.main.dialog_confirm_layout.*

class DeleteConfirmDialog(context: Context, title: String, content: String) : Dialog(context) {
    private var mOnclickLiener: OnDeleteClickLiener? = null
    private var mTitle: String = title
    private var mContent: String = content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm_layout)

        initView()
    }

    private fun initView() {

        confirm_btn.setOnClickListener {
            if (mOnclickLiener != null) {
                mOnclickLiener?.onClickConfirm()
            }
        }

        cancel_btn.setOnClickListener {
            if (mOnclickLiener != null) {
                mOnclickLiener?.onClickCancel()
            }
        }
        confirm_title.setText(mTitle)
        confirm_content.setText(mContent)
    }

    fun setOnclickLiener(onclickLiener: OnDeleteClickLiener) {
        mOnclickLiener = onclickLiener
    }

    interface OnDeleteClickLiener {
        fun onClickConfirm()
        fun onClickCancel()
    }

}