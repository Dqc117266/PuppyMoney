package com.dqc.todo.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.dqc.todo.R
import com.dqc.todo.database.AppDataBase
import com.dqc.todo.database.bean.TodoListBean
import com.dqc.todo.ui.adapter.TodoListAdapter
import com.dqc.todo.ui.dialog.DeleteConfirmDialog
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), TodoListAdapter.IOnClickItemCallback {

    private var mTodoListAdapter: TodoListAdapter? = null
    private var mTimerReceiver: TimerReceive? = null
    private var mMV: MMKV? = null
    private var mMoney: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        updateMoney()

        val todoListDao = AppDataBase.getInstance(this).getTodoListDao()
         todoListDao.getTodoList()

        todo_list_rv.layoutManager = LinearLayoutManager(this)
        mTodoListAdapter = TodoListAdapter(this)
        todo_list_rv.adapter = mTodoListAdapter
        mTodoListAdapter?.refreshDate()
        mTodoListAdapter?.setOnClickItemCallback(this)


        create_task_btn.setOnClickListener {
            var intent = Intent(this, CreateTaskActivity::class.java)
            startActivityForResult(intent, 1)
        }

        registerBroadcast()
    }

    private fun updateMoney() {
        mMV = MMKV.defaultMMKV()
        mMoney = mMV?.decodeDouble("all_money_size", 0.0)!!
        main_all_money_tv.setText("$mMoney")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mTodoListAdapter != null) {
            mTodoListAdapter?.refreshDate()
        }
    }

    override fun onClickItem(todoBean: TodoListBean) {
        var intent = Intent(this, TodoTaskActivity::class.java)
        intent.putExtra("todo_bean", todoBean)
        startActivity(intent)
    }

    override fun onLongClickItem(view: View, position: Int, todoList: TodoListBean) {

        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_item, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {

                val deleteConfirmDialog = DeleteConfirmDialog(this@MainActivity, "删除",getString(R.string.dialog_delete_text, todoList.todoTitle))
                deleteConfirmDialog.setOnclickLiener(object: DeleteConfirmDialog.OnDeleteClickLiener {

                    override fun onClickConfirm() {
                        val todoListDao = AppDataBase.getInstance(this@MainActivity).getTodoListDao()
                        todoListDao.deleteTodo(todoList)

                        mTodoListAdapter?.refreshDate()
                        deleteConfirmDialog.dismiss()
                    }

                    override fun onClickCancel() {
                        deleteConfirmDialog.dismiss()
                    }
                })
                deleteConfirmDialog.show()
                return false
            }
        })
        popupMenu.show()
    }

    private fun registerBroadcast() {
        var timerReceiver = TimerReceive()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.dqc.timer")
        registerReceiver(timerReceiver, intentFilter)
    }

    private fun stopReciver() {
        if (mTimerReceiver != null) {
            unregisterReceiver(mTimerReceiver)
            mTimerReceiver = null
        }
    }

    inner class TimerReceive: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action.equals("com.dqc.timer")) {

                if (intent!!.hasExtra("task_ok")) {
                    updateMoney()
                    stopReciver()
                }
            }
        }

    }

}