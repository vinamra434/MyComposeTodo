package com.univ.mytodo.data.repository

import com.univ.mytodo.data.local.TodoDao
import com.univ.mytodo.data.model.Todo
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(private val todoDao: TodoDao) {

    val getAllTodoLive = todoDao.getAllTodoLive()

    fun getTodos(isCompleted: Boolean?) = todoDao.getTodos(isCompleted)

    suspend fun upsertTodo(todo: Todo): Long =
        todoDao.upsert(todo)


    suspend fun deleteTodo(id: Int) {
        Dispatchers.IO.apply {
            todoDao.delete(id)
        }
    }

    suspend fun getTodo(id: Int): Todo =
        todoDao.getTodo(id)


}