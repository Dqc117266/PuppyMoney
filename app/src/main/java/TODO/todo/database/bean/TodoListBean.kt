package com.dqc.todo.database.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "TodoListBean")
data class TodoListBean(
    @PrimaryKey(autoGenerate = true) val tid: Int,
    @ColumnInfo(name = "todo_title") val todoTitle: String,
    @ColumnInfo(name = "todo_duration") val todoDuration: String,
    @ColumnInfo(name = "todo_reward_amount") val todoRewardAmount: String,
    @ColumnInfo(name = "todo_today_is_complete") val isComplete: Boolean): Parcelable
