package com.maronworks.roomdatabase.home.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.maronworks.roomdatabase.home.model.Todo

@Dao
interface TodoDao {
    @Query("SELECT * FROM TODO")
    fun getAllTodo() : LiveData<List<Todo>>

    @Insert
    fun addTodo(todo: Todo)

    @Upsert
    fun updateTodo(todo: Todo)

    @Query("DELETE FROM TODO WHERE id=:id")
    fun deleteTodo(id: Int)
}