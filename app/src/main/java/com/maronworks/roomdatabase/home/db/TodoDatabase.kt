package com.maronworks.roomdatabase.home.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maronworks.roomdatabase.home.model.Todo

@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    companion object {
        const val NAME = "todo_db"
    }

    abstract fun getTodoDao(): TodoDao
}