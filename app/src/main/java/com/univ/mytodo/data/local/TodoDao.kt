package com.univ.mytodo.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.univ.mytodo.data.model.Todo

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_table")
    fun getAllTodoLive(): LiveData<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(todo: Todo): Long

    @Query("DELETE From todo_table WHERE id= :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM todo_table WHERE id= :id")
    suspend fun getTodo(id: Int): Todo

    @Query("SELECT * FROM todo_table WHERE IIF(:isCompleted IS NULL, 1, isCompleted = :isCompleted)")
    fun getTodos(isCompleted: Boolean?): LiveData<List<Todo>>

}