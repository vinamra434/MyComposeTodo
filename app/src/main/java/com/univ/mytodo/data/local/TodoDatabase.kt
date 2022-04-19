package com.univ.mytodo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.univ.mytodo.data.model.Todo

@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase: RoomDatabase() {
    abstract fun getTodoDao(): TodoDao
}