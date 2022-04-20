package com.univ.mytodo.util

import java.text.DecimalFormat

object FormatterUtil {

    fun getFormattedValue(value: Int): String {
        val decimalFormat = DecimalFormat("00")
        var formattedValue = value.toString()
        if (formattedValue.length == 1) {
            formattedValue = decimalFormat.format(value)
        }
        return formattedValue
    }

    fun getTimeMode(hour: Int): Constants.TimeMode {
        return if (hour in 12..23) {
            Constants.TimeMode.PM
        } else {
            Constants.TimeMode.AM
        }
    }

    fun convert24To12(hour: Int): String {
        var convertHour = hour
        if (hour > 12) {
            convertHour -= 12
        }

        if (hour == 0) {
            convertHour = 12
        }

        return getFormattedValue(convertHour)
    }
}