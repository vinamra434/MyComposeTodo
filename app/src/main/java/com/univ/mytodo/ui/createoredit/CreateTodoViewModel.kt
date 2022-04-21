package com.univ.mytodo.ui.createoredit

import android.util.Log
import androidx.lifecycle.*
import com.univ.mytodo.data.model.Todo
import com.univ.mytodo.data.repository.TodoRepository
import com.univ.mytodo.util.*
import com.univ.mytodo.util.FormatterUtil.convert24To12
import com.univ.mytodo.util.FormatterUtil.getFormattedValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class CreateTodoViewModel @Inject constructor(private val todoRepository: TodoRepository) :
    ViewModel() {

    private val validationsList: MutableLiveData<List<Validation>> = MutableLiveData()

    val closeActivity: MutableLiveData<Boolean> = MutableLiveData()

    val titleField: MutableLiveData<String> = MutableLiveData()
    val descField: MutableLiveData<String> = MutableLiveData()
    val dueDayField: MutableLiveData<String> = MutableLiveData()
    val dueMonthField: MutableLiveData<String> = MutableLiveData()
    val dueYearField: MutableLiveData<String> = MutableLiveData()
    val dueMinuteField: MutableLiveData<String> = MutableLiveData()
    val dueHourField: MutableLiveData<String> = MutableLiveData()
    val dueModeField: MutableLiveData<Constants.TimeMode> = MutableLiveData()

    val titleError: MutableLiveData<Boolean> = MutableLiveData()
    val titleErrorMessage: MutableLiveData<String> = MutableLiveData()
    val timeError: MutableLiveData<Boolean> = MutableLiveData()
    val timeErrorMessage: MutableLiveData<String> = MutableLiveData()
    val dateError: MutableLiveData<Boolean> = MutableLiveData()
    val dateErrorMessage: MutableLiveData<String> = MutableLiveData()

    val titleValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.TITLE)
    val dateValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.DUE_DATE)
    val timeValidation: LiveData<Resource<Int>> = filterValidation(Validation.Field.DUE_TIME)

    /*whenever new data is available in validationlist Transformation operation is applied here*/
    private fun filterValidation(field: Validation.Field) =
        Transformations.map(validationsList) {
            it.find { validation -> validation.field == field }
                ?.run { return@run this.resource }
                ?: Resource.unknown()
        }

    fun onTitleChange(title: String) {
        titleField.postValue(title)
    }

    fun onDescChange(description: String) {
        descField.postValue(description)
    }

    fun onDueDayChange(day: String) {
        dueDayField.postValue(day)
    }

    fun onDueMonthChange(month: String) {
        dueMonthField.postValue(month)
    }

    fun onDueYearChange(year: String) {
        dueYearField.postValue(year)
    }

    fun onDueMinuteChange(minute: String) {
        dueMinuteField.postValue(minute)
    }

    fun onDueHourChange(second: String) {
        dueHourField.postValue(second)
    }

    fun onDueModeChange(value: Constants.TimeMode) {
        dueModeField.postValue(value)
    }

    fun setTitleError(bool: Boolean, errorMessage: String = "") {
        titleError.postValue(bool)
        titleErrorMessage.postValue(errorMessage)
    }

    fun setDateError(bool: Boolean, errorMessage: String = "") {
        dateError.postValue(bool)
        dateErrorMessage.postValue(errorMessage)
    }

    fun setTimeError(bool: Boolean, errorMessage: String = "") {
        timeError.postValue(bool)
        timeErrorMessage.postValue(errorMessage)
    }

    //save new _todo or save edited _todo
    fun onSave(id: Int) {
        val title = titleField.value?.trim()
        val desc = descField.value?.trim() //desc is optional field hence empty if default

        var dueDay: Int? = null
        var dueMonth: Int? = null
        var dueYear: Int? = null
        var dueMinute: Int? = null
        var dueHour: Int? = null

        try {
            dueDay = dueDayField.value?.trim()?.toInt()
            dueMonth = dueMonthField.value?.trim()?.toInt()
            dueYear = dueYearField.value?.trim()?.toInt()
            dueMinute = dueMinuteField.value?.trim()?.toInt()
            dueHour = dueHourField.value?.trim()?.toInt()
        } catch (e: NumberFormatException) {
            Log.i("CreateTodoViewModel", "onSave exception in conversion of string to int e = $e")
        }

        //converting 12hr format to 24hr format for storing in db
        dueModeField.value.run {

            if (dueHour != null) {
                if (this == Constants.TimeMode.PM && dueHour!! < 12) {
                    dueHour = dueHour!!.plus(12)
                }

                if (this == Constants.TimeMode.AM && dueHour == 12) {
                    dueHour = 0
                }
            }
        }

        val validations =
            Validator.validateTodoFields(title, dueDay, dueMonth, dueYear, dueMinute, dueHour)
        validationsList.postValue(validations)

        if (validations.isNotEmpty()
            && title != null
            && desc != null
            && dueDay != null
            && dueMonth != null
            && dueYear != null
            && dueMinute != null
            && dueHour != null
        ) {
            val successValidation = validations.filter { it.resource.status == Status.SUCCESS }
            if (successValidation.size == validations.size) {
                //save in db
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val todo = Todo(
                            id = if (id == -1/*this means we are creating new_todo*/) 0 else id /*this means we are editing old_todo*/, //if id is available then save edited _todo
                            title = title,
                            desc = desc,
                            date = getFormattedDate(
                                dueDay,
                                dueMonth,
                                dueYear.toString()
                            ),
                            isCompleted = false,
                            time = "${getFormattedValue(dueHour!!)}:${getFormattedValue(dueMinute)}"
                        )
                        todoRepository.upsertTodo(todo)
                        Log.d("CreateTodoViewModel", "todo inserted is $todo")
                        closeActivity.postValue(true)

                    } catch (e: Exception) {
                        Log.d(
                            "CreateTodoViewModel",
                            "onSave: exception caught for insert todo operation $e"
                        )
                    }
                }
            }
        }
    }


    private fun getFormattedDate(dueDay: Int, dueMonth: Int, dueYear: String): String {
        val decimalFormat = DecimalFormat("00")

        var formattedDay = dueDay.toString()
        if (formattedDay.length == 1) {
            formattedDay = decimalFormat.format(dueDay)
        }

        var formattedMonth = dueMonth.toString()
        if (formattedMonth.length == 1) {
            formattedMonth = decimalFormat.format(dueMonth)
        }

        return "$formattedMonth/$formattedDay/$dueYear"
    }


    fun loadTodoData(id: Int) {
        Log.i("CreateTodoViewModel", "loadTodoData")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val todo = todoRepository.getTodo(id)

                titleField.postValue(todo.title)
                descField.postValue(todo.desc)

                val splitDate: List<String> = todo.date.split("/")
                val splitDueMonth = splitDate[0]
                val splitDueDay = splitDate[1]
                val splitDueYear = splitDate[2]
                dueMonthField.postValue(splitDueMonth)
                dueDayField.postValue(splitDueDay)
                dueYearField.postValue(splitDueYear)

                val splitTime: List<String> = todo.time.split(":")
                val splitHour = splitTime[0].toInt()
                val splitMinute = splitTime[1].toInt()

                val mode = FormatterUtil.getTimeMode(splitHour)
                dueModeField.postValue(mode)

                dueHourField.postValue(convert24To12(splitHour)) //convert 24hr to 12hr format
                dueMinuteField.postValue(getFormattedValue(splitMinute))

            } catch (e: Exception) {
                Log.i("CreateTodoViewModel", "exception caught in loadTodoData e = $e")
            }
        }
    }
}