package com.dqc.todo.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dqc.todo.R
import com.dqc.todo.database.AppDataBase
import com.dqc.todo.database.bean.TodoListBean
import com.dqc.todo.utils.minute2HourAndMinute

class TodoListAdapter(context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mContext: Context = context
    private var mTodoList: List<TodoListBean> = ArrayList()
    private var mIClickItem: IOnClickItemCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.todo_list_rv_item, parent, false)
            return ViewHolder(view)
        } else {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.empty_rv_item, parent, false)
            return EmptyiewHolder(view)
        }
    }

    fun setOnClickItemCallback(iclickItem: IOnClickItemCallback) {
        mIClickItem = iclickItem
    }

    fun refreshDate() {
        val todoListDao = AppDataBase.getInstance(mContext).getTodoListDao()
        mTodoList = todoListDao.getTodoList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (position < mTodoList.size) {
            holder as ViewHolder
            holder.mTodoTitle.setText(mTodoList.get(position).todoTitle)
            holder.mTodoDuration.setText(mContext.minute2HourAndMinute(mTodoList.get(position).todoDuration.toInt()))
            holder.mTodoStatus.setText("+" + mTodoList.get(position).todoRewardAmount)
            holder.itemView.setOnClickListener {
                if (mIClickItem != null) {
                    mIClickItem?.onClickItem(mTodoList.get(position))
                }
            }

            holder.itemView.setOnLongClickListener(object :View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    if (mIClickItem != null) {
                        mIClickItem?.onLongClickItem(holder.itemView, position, mTodoList.get(position))
                    }
                    return true
                }

            })
        }
    }

    override fun getItemCount(): Int {
        if (mTodoList == null) {
            return 0
        } else {
            return mTodoList.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < mTodoList.size) {
            return 0
        } else {
            return 1
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var mTodoTitle: TextView
        var mTodoDuration: TextView
        var mTodoStatus: TextView

        init {
            mTodoTitle = itemView.findViewById(R.id.todo_title)
            mTodoDuration = itemView.findViewById(R.id.todo_duration)
            mTodoStatus = itemView.findViewById(R.id.todo_status)
        }
    }

    class EmptyiewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    interface IOnClickItemCallback {
        fun onClickItem(todoList: TodoListBean)
        fun onLongClickItem(view: View, position: Int, todoList: TodoListBean)
    }

}