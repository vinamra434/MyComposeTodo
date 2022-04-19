package com.univ.mytodo.util

import com.univ.mytodo.R
import java.text.SimpleDateFormat
import java.util.*

object Validator {

    fun validateTodoFields(
        title: String?,
        dueDay: Int?,
        dueMonth: Int?,
        dueYear: Int?,
        dueMinute: Int?,
        dueHour: Int?
    ): List<Validation> =
        ArrayList<Validation>().apply {
            when {
                title.isNullOrBlank() ->
                    add(
                        Validation(
                            Validation.Field.TITLE,
                            Resource.error(R.string.err_empty_title)
                        )
                    )
                else ->
                    add(Validation(Validation.Field.TITLE, Resource.success()))
            }
            when {
                dueDay == null || dueMonth == null || dueYear == null ->
                    add(
                        Validation(
                            Validation.Field.DUE_DATE,
                            Resource.error(R.string.err_empty_day)
                        )
                    )

                isDueDateValid(dueDay, dueMonth, dueYear) ->
                    add(Validation(Validation.Field.DUE_DATE, Resource.success()))
                else -> add(
                    Validation(
                        Validation.Field.DUE_DATE,
                        Resource.error(R.string.err_empty_day)
                    )
                )
            }
            when {
                dueHour == null || dueMinute == null ->
                    add(
                        Validation(
                            Validation.Field.DUE_TIME,
                            Resource.error(R.string.err_empty_time)
                        )
                    )
                !isTimeFormatValid(
                    dueHour,
                    dueMinute
                ) ->
                    add(
                        Validation(
                            Validation.Field.DUE_TIME,
                            Resource.error(R.string.err_incorrect_due_time_format)
                        )
                    )

                dueDay != null &&
                        dueMonth != null &&
                        dueYear != null &&
                        !isDueTimeValid(
                            dueHour,
                            dueMinute,
                            dueDay, dueMonth, dueYear
                        )
                ->
                    add(
                        Validation(
                            Validation.Field.DUE_TIME,
                            Resource.error(R.string.err_incorrect_due_time)
                        )
                    )

                else -> add(
                    Validation(
                        Validation.Field.DUE_TIME,
                        Resource.success()
                    )
                )
            }
        }

    private fun isTimeFormatValid(dueHour: Int, dueMinute: Int): Boolean {
        return dueHour in 0..23 && dueMinute in 0..59
    }

    //returns true if due time is in future
    private fun isDueTimeValid(
        dueHour: Int,
        dueMinute: Int,
        dueDay: Int,
        dueMonth: Int,
        dueYear: Int,
    ): Boolean {

        //if due date equals current date then time has to be ahead of current time
        return if (dueYear == getCurrentYear() && dueMonth == getCurrentMonth() && dueDay == getCurrentDay()) {
            (dueHour == getCurrentTimeHour() && dueMinute > getCurrentTimeMinute()) ||
                    (dueHour > getCurrentTimeHour())
        }
        //if due date is ahead of current date then return true as time can be anything
        else (dueYear > getCurrentYear() ||
                dueYear == getCurrentYear() && dueMonth > getCurrentMonth() ||
                dueYear == getCurrentYear() && dueMonth == getCurrentMonth() && dueDay > getCurrentDay())

    }


    //returns true if due date is in future else false
    private fun isDueDateValid(dueDay: Int, dueMonth: Int, dueYear: Int): Boolean {

        val currentYear = getCurrentYear()
        val currentMonth = getCurrentMonth()
        val currentDay = getCurrentDay()

        if (currentYear == dueYear) {
            if (currentMonth == dueMonth) {
                return when (dueMonth) {
                    2 -> {
                        if (isLeapYear(dueYear)) {
                            dueDay in (currentDay)..29
                        } else {
                            dueDay in (currentDay)..28
                        }
                    }
                    4, 6, 9, 11 -> {
                        dueDay in (currentDay)..30
                    }
                    1,3,5,7,8,10,12 -> {
                        dueDay in (currentDay)..31
                    }
                    else -> {
                        false
                    }
                }
            } else if (dueMonth > currentMonth) {

                return when (dueMonth) {
                    2 -> {
                        if (isLeapYear(dueYear)) {
                            dueDay in 1..29
                        } else {
                            dueDay in 1..28
                        }
                    }
                    4, 6, 9, 11 -> {
                        dueDay in 1..30
                    }
                    1,3,5,7,8,10,12 -> {
                        dueDay in 1..31
                    }
                    else -> {
                        false
                    }
                }
            }
        } else if (dueYear > currentYear) {
            return when (dueMonth) {
                2 -> {
                    if (isLeapYear(dueYear)) {
                        dueDay in 1..29
                    } else {
                        dueDay in 1..28
                    }
                }
                4, 6, 9, 11 -> {
                    dueDay in 1..30
                }
                1,3,5,7,8,10,12 -> {
                    dueDay in 1..31
                }
                else -> {
                    false
                }
            }
        }
        return false
    }

    private fun getCurrentDay() = SimpleDateFormat("dd").format(Calendar.getInstance().time).toInt()

    private fun getCurrentMonth() =
        SimpleDateFormat("MM").format(Calendar.getInstance().time).toInt()

    private fun getCurrentYear() =
        SimpleDateFormat("yyyy").format(Calendar.getInstance().time).toInt()


    private fun getCurrentTimeMinute() =
        SimpleDateFormat("mm").format(Calendar.getInstance().time).toInt()


    private fun getCurrentTimeHour() =
        SimpleDateFormat("HH").format(Calendar.getInstance().time).toInt()


    private fun isLeapYear(dueYear: Int): Boolean {
        return (((dueYear % 4 == 0) &&
                (dueYear % 100 != 0)) ||
                (dueYear % 400 == 0))
    }
}

data class Validation(val field: Field, val resource: Resource<Int>) {

    enum class Field {
        TITLE,
        DUE_DATE,
        DUE_TIME,
    }
}
