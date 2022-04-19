package com.univ.mytodo.util

object Constants {

    val INTENT_KEY_TODO = "todo_id"

    val MAX_LENGTH_2 = 3
    val MAX_LENGTH_4 = 5

    enum class TimeMode(val value: Boolean) {
        AM(true),
        PM(false)
    }
}