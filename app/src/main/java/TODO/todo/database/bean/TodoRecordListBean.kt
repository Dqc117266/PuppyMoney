package com.dqc.todo.database.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "TodoRecordListBean")
data class TodoRecordListBean(
    @PrimaryKey(autoGenerate = true) val tid: Int,
    @ColumnInfo(name = "todo_title") val todoTitle: String,
    @ColumnInfo(name = "todo_complele_time") val completeTime: String,
    @ColumnInfo(name = "todo_amount") val amount: String): Parcelable
