package com.univ.mytodo.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.univ.mytodo.data.model.Todo
import com.univ.mytodo.data.repository.TodoRepository
import com.univ.mytodo.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val todoType = MutableLiveData(Constants.TodoType.ALL)

    val allTodoList: LiveData<List<Todo>> = Transformations.switchMap(todoType) { newType ->

        val typeAsBoolean = when(newType) {
            Constants.TodoType.ALL -> null
            Constants.TodoType.COMPLETED -> true
            Constants.TodoType.INCOMPLETE -> false
            else -> throw IllegalArgumentException("Not a possible value")
        }

        todoRepository.getTodos(typeAsBoolean)
    }

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

    fun sort(isVisible: Boolean) {
        isSortExpanded.postValue(isVisible)
    }


    fun onFilterClick(type: Constants.TodoType) {
        todoType.postValue(type)
    }
}