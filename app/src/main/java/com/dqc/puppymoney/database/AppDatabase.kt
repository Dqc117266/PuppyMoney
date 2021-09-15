package com.dqc.puppymoney.database

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dqc.puppymoney.bean.WishPhotoAlbumBean
import com.dqc.puppymoney.bean.WishToChooseBean
import com.dqc.puppymoney.dao.WishListDao
import java.io.File

@Database(entities = [WishToChooseBean::class, WishPhotoAlbumBean::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCEL: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCEL ?: synchronized(this) {
                INSTANCEL ?: buildDatabase(context).also { INSTANCEL = it }
        }
        private fun buildDatabase(context: Context): AppDatabase {
//            val dbDir = File(Environment.getExternalStorageDirectory(), "RoomDatabase")
//            if (dbDir.exists()) {
//                dbDir.mkdir()
//            }
//            val dbFile = File(dbDir, "wish.db")
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "wish.db")
                .allowMainThreadQueries()
                .build()
        }

    }

    abstract fun getWishListDao(): WishListDao
}