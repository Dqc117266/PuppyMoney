package com.dqc.todo.database.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "MoneyRecordBean")
data class MoneyRecordBean(
    @PrimaryKey(autoGenerate = true) val tid: Int,
    @ColumnInfo(name = "all_money") val allMoney: Double): Parcelable