package com.dqc.todo.ui.activity

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TimePicker
import android.widget.Toast
import com.dqc.todo.R
import com.dqc.todo.database.AppDataBase
import com.dqc.todo.database.bean.TodoListBean
import com.dqc.todo.utils.minute2HourAndMinute
import com.dqc.todo.ui.dialog.GoldSettingDialog
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_create_task.*

class CreateTaskActivity : BaseActivity() {

    private var mIsTaskDuraComplete = false
    private var mIsTaskGoldComplete = false
    private var mTaskMinute: String? = null
    private var mTaskGold: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        mImmersionBar = ImmersionBar.with(this)
            .statusBarDarkFont(true)
        mImmersionBar.init()

        initView()
    }

    private fun initView() {

        task_duration.setOnClickListener {
            var packDialog = TimePickerDialog(this, object: TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

                    if (hourOfDay + minute > 0) {
                        mTaskMinute = "${hourOfDay * 60 + minute}"
                        crate_task_duration_tv.setText(this@CreateTaskActivity.minute2HourAndMinute(hourOfDay * 60 + minute))
                        mIsTaskDuraComplete = true
                    } else {
                        Toast.makeText(this@CreateTaskActivity, "时间不可以为零", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("OnTimeSet", " ${hourOfDay} ${minute} ")
                }
            }, 0, 0, true)
            packDialog.show()
        }

        task_reward_amount_tv.setOnClickListener {
            val goldSettingDialog = GoldSettingDialog(this)
            goldSettingDialog.setConfirmCallBack(object :
                GoldSettingDialog.IClickConfirmCallBack {
                override fun onClickConfirm(goldSize: String) {
                    if (goldSize != null && goldSize.length > 0) {
                        mIsTaskGoldComplete = true
                        mTaskGold = goldSize
                        crate_task_reward_tv.setText("${goldSize}金币")
                    }
                }
            })
            goldSettingDialog.show()
        }

        create_task_btn.setOnClickListener {
            if (mIsTaskDuraComplete
                && mIsTaskGoldComplete
                && (create_edit_title.text.toString() != null
                        && create_edit_title.text.toString().length > 1)) {
                var title = create_edit_title.text.toString()
                val todoListDao = AppDataBase.getInstance(this).getTodoListDao()
                var todoBean = TodoListBean(0, title, mTaskMinute!!, mTaskGold!!, false)
                todoListDao.insertTodo(todoBean)

                val intent = Intent()
                setResult(1, intent)
                finish()
            } else {
                Toast.makeText(this, "还未设置完成", Toast.LENGTH_SHORT).show()
            }
        }

        close_page_iv.setOnClickListener {
            val mInputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), 0)

            onBackPressed()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(null != this.getCurrentFocus()) {
            val mInputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            return mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), 0)
        }
        return false
    }

}