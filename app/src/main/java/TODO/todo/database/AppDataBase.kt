package com.dqc.todo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dqc.todo.database.bean.MoneyRecordBean
import com.dqc.todo.database.bean.TodoListBean
import com.dqc.todo.database.bean.TodoRecordListBean
import com.dqc.todo.database.dao.TodoListDao

@Database(entities = [TodoListBean::class, TodoRecordListBean::class, MoneyRecordBean::class], version = 1)
abstract class AppDataBase: RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCEL: AppDataBase? = null
        fun getInstance(context: Context): AppDataBase =
            INSTANCEL ?: synchronized(this) {
                INSTANCEL ?: buildDataBase(context).also {
                    INSTANCEL = it
                }
            }

        private fun buildDataBase(context: Context): AppDataBase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java, "todo.db")
                .allowMainThreadQueries()
                .build()
        }
    }

    abstract fun getTodoListDao(): TodoListDao
}