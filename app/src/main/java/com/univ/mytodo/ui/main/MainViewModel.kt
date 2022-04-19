package com.univ.mytodo.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.mytodo.data.model.Todo
import com.univ.mytodo.data.repository.TodoRepository
import com.univ.mytodo.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    val allTodoList: LiveData<List<Todo>> = todoRepository.getAllTodoLive

    val isSortExpanded: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                todoRepository.deleteTodo(todo.id)
            } catch (e: Exception) {
                Log.d(
                    "MainViewModel",
                    "exception caught for delete todo operation $e"
                )
            }
        }
    }

    fun changeMarkAsStatus(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                todoRepository.upsertTodo(todo)
            } catch (e: Exception) {
                Log.d(
                    "MainViewModel",
                    "markAsComplete: exception caught for upsertTodo todo operation $e"
                )
            }
        }
    }

    fun onSortExpandedChange(value: Boolean) {
        isSortExpanded.postValue(value)
    }
}