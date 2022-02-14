package com.dqc.todo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dqc.todo.database.bean.TodoListBean
import com.dqc.todo.database.bean.TodoRecordListBean

@Dao
interface TodoListDao {

    @Query("SELECT * FROM todolistBean")
    fun getTodoList(): List<TodoListBean>

    @Insert
    fun insertTodo(todo: TodoListBean)

    @Delete
    fun deleteTodo(todo: TodoListBean)

    @Delete
    fun deleteTodoList(todoList: List<TodoListBean>)

    @Query("SELECT * FROM TodoRecordListBean order by tid asc")
    fun getTodoTodoRecordList():List<TodoRecordListBean>

    @Insert
    fun insertTodoTodoRecord(todo: TodoRecordListBean)

    @Delete
    fun deleteTodoRecord(todoRecord: TodoRecordListBean)

    @Delete
    fun deleteTodoRecordAll(todoRecordList: List<TodoRecordListBean>)

}