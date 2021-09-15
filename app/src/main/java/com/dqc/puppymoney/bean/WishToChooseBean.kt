package com.dqc.puppymoney.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "WishToChooseBean")
data class WishToChooseBean(
    @PrimaryKey(autoGenerate = true) val wid: Int,
    @ColumnInfo(name = "wish_index") var index: Int,
    @ColumnInfo(name = "wish_text") var wishText: String,
    @ColumnInfo(name = "is_selected") var isSelected: Boolean): Parcelable
