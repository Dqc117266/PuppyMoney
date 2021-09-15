package com.dqc.puppymoney.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WishPhotoAlbumBean")
data class WishPhotoAlbumBean(
    @PrimaryKey(autoGenerate = true) val wid: Int,
    @ColumnInfo(name = "wish_txt") var wishText: String,
    @ColumnInfo(name = "img_path") var imgPath: String,
    @ColumnInfo(name = "md5_value") var md5Value: String?,
    @ColumnInfo(name = "add_img_year") var mAddYear: String,
    @ColumnInfo(name = "add_img_month_day") var mAddMonthDay: String,
    @ColumnInfo(name = "add_img_hour_minute") var mAddHourMinute: String) {
}